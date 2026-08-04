package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.ExamEntity
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

private const val DASHBOARD_STATE_TIMEOUT_MILLIS = 5_000L
private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1_000L
private const val RECENT_RECORDS_LIMIT = 10
private const val RECENT_EXAMS_LIMIT = 5

data class QuickResumeInfo(
    val examId: Long,
    val examName: String,
    val subjectName: String,
    val lastStudiedMillis: Long
)

class DashboardViewModel(
    examRepository: ExamRepository,
    studyRecordRepository: StudyRecordRepository,
    subjectRepository: SubjectRepository
) : ViewModel() {

    private val sharingStarted =
        SharingStarted.WhileSubscribed(
            DASHBOARD_STATE_TIMEOUT_MILLIS
        )

    private val allExams: StateFlow<List<ExamEntity>> =
        examRepository
            .getAllExams()
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    private val allRecords: StateFlow<List<StudyRecordEntity>> =
        studyRecordRepository
            .getAllStudyRecords()
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    private val allSubjects =
        subjectRepository
            .getAllSubjects()
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val recentExams: StateFlow<List<ExamEntity>> =
        allExams
            .map { exams ->
                exams
                    .sortedByDescending {
                        it.createdAtMillis
                    }
                    .take(RECENT_EXAMS_LIMIT)
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val upcomingExams: StateFlow<List<ExamEntity>> =
        allExams
            .map { exams ->
                val todayStart =
                    startOfDay(System.currentTimeMillis())

                exams
                    .filter { exam ->
                        val examDate = exam.examDateMillis

                        examDate != null &&
                                examDate >= todayStart
                    }
                    .sortedBy { exam ->
                        exam.examDateMillis
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val todaysStudyMinutes: StateFlow<Int> =
        allRecords
            .map { records ->
                val todayStart =
                    startOfDay(System.currentTimeMillis())

                val tomorrowStart =
                    todayStart + ONE_DAY_MILLIS

                sumMinutesInRange(
                    records = records,
                    startMillis = todayStart,
                    endMillis = tomorrowStart
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val todaysGoalPercentage: StateFlow<Float> =
        combine(
            allRecords,
            allExams
        ) { records, exams ->
            val todayStart =
                startOfDay(System.currentTimeMillis())

            val tomorrowStart =
                todayStart + ONE_DAY_MILLIS

            val todaysSolvedQuestions =
                records
                    .filter { record ->
                        record.recordDateMillis in
                                todayStart until tomorrowStart
                    }
                    .sumOf { record ->
                        record.correctCount +
                                record.wrongCount +
                                record.blankCount
                    }

            val totalDailyGoal =
                exams.sumOf { exam ->
                    exam.dailyQuestionGoal
                }

            if (totalDailyGoal <= 0) {
                0f
            } else {
                (
                        todaysSolvedQuestions.toFloat() /
                                totalDailyGoal.toFloat()
                        ) * 100f
            }
        }.stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = 0f
        )

    val currentStreak: StateFlow<Int> =
        allRecords
            .map { records ->
                calculateCurrentStreak(records)
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val weeklyMinutes: StateFlow<Int> =
        allRecords
            .map { records ->
                val todayStart =
                    startOfDay(System.currentTimeMillis())

                val rangeStart =
                    todayStart - (6L * ONE_DAY_MILLIS)

                val rangeEnd =
                    todayStart + ONE_DAY_MILLIS

                sumMinutesInRange(
                    records = records,
                    startMillis = rangeStart,
                    endMillis = rangeEnd
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val monthlyMinutes: StateFlow<Int> =
        allRecords
            .map { records ->
                val todayStart =
                    startOfDay(System.currentTimeMillis())

                val rangeStart =
                    todayStart - (29L * ONE_DAY_MILLIS)

                val rangeEnd =
                    todayStart + ONE_DAY_MILLIS

                sumMinutesInRange(
                    records = records,
                    startMillis = rangeStart,
                    endMillis = rangeEnd
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val recentStudyRecords:
            StateFlow<List<StudyRecordEntity>> =
        allRecords
            .map { records ->
                records
                    .sortedByDescending {
                        it.recordDateMillis
                    }
                    .take(RECENT_RECORDS_LIMIT)
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val quickResume: StateFlow<QuickResumeInfo?> =
        combine(
            allRecords,
            allExams,
            allSubjects
        ) { records, exams, subjects ->
            val lastRecord =
                records.maxByOrNull {
                    it.recordDateMillis
                } ?: return@combine null

            val exam =
                exams.firstOrNull {
                    it.id == lastRecord.examId
                } ?: return@combine null

            val subject =
                subjects.firstOrNull {
                    it.id == lastRecord.subjectId
                }

            QuickResumeInfo(
                examId = exam.id,
                examName = exam.name,
                subjectName = subject?.name.orEmpty(),
                lastStudiedMillis =
                    lastRecord.recordDateMillis
            )
        }.stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = null
        )

    private fun sumMinutesInRange(
        records: List<StudyRecordEntity>,
        startMillis: Long,
        endMillis: Long
    ): Int {
        return records
            .filter { record ->
                record.recordDateMillis in
                        startMillis until endMillis
            }
            .sumOf { record ->
                record.durationMinutes
            }
    }

    private fun calculateCurrentStreak(
        records: List<StudyRecordEntity>
    ): Int {
        if (records.isEmpty()) {
            return 0
        }

        val studiedDays =
            records
                .map { record ->
                    startOfDay(record.recordDateMillis)
                }
                .toHashSet()

        var cursor =
            startOfDay(System.currentTimeMillis())

        /*
         * Kullanıcı bugün henüz çalışmadıysa,
         * seriyi dünden itibaren hesaplarız.
         */
        if (!studiedDays.contains(cursor)) {
            cursor -= ONE_DAY_MILLIS
        }

        var streak = 0

        while (studiedDays.contains(cursor)) {
            streak++
            cursor -= ONE_DAY_MILLIS
        }

        return streak
    }

    private fun startOfDay(
        millis: Long
    ): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
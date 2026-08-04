package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val SUBJECT_STATS_TIMEOUT_MILLIS = 5_000L
private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1_000L
private const val ONE_WEEK_MILLIS = 7L * ONE_DAY_MILLIS

class SubjectStatisticsViewModel(
    private val subjectId: Long,
    subjectRepository: SubjectRepository,
    examRepository: ExamRepository,
    studyRecordRepository: StudyRecordRepository
) : ViewModel() {

    private val sharingStarted =
        SharingStarted.WhileSubscribed(
            SUBJECT_STATS_TIMEOUT_MILLIS
        )

    val subject: StateFlow<SubjectEntity?> =
        subjectRepository
            .getSubjectById(subjectId)
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = null
            )

    private val recordsForSubject:
            StateFlow<List<StudyRecordEntity>> =
        studyRecordRepository
            .getAllStudyRecords()
            .map { records ->
                records.filter { record ->
                    record.subjectId == subjectId
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    private val examNetRule:
            StateFlow<String?> =
        subject
            .flatMapLatest { currentSubject ->
                if (currentSubject == null) {
                    flowOf(null)
                } else {
                    examRepository
                        .getExamById(
                            currentSubject.examId
                        )
                        .map { exam ->
                            exam?.netCalculationRule
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = null
            )

    val sessionCount: StateFlow<Int> =
        recordsForSubject
            .map { records ->
                records.size
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val totalMinutes: StateFlow<Int> =
        recordsForSubject
            .map { records ->
                records.sumOf { record ->
                    record.durationMinutes
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val totalQuestions: StateFlow<Int> =
        recordsForSubject
            .map { records ->
                records.sumOf { record ->
                    record.correctCount +
                            record.wrongCount +
                            record.blankCount
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0
            )

    val averageStudyTimeMinutes:
            StateFlow<Double> =
        recordsForSubject
            .map { records ->
                if (records.isEmpty()) {
                    0.0
                } else {
                    records
                        .sumOf { record ->
                            record.durationMinutes
                        }
                        .toDouble() /
                            records.size.toDouble()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0.0
            )

    val averageNet: StateFlow<Double> =
        combine(
            recordsForSubject,
            examNetRule
        ) { records, rule ->
            if (records.isEmpty()) {
                0.0
            } else {
                records.sumOf { record ->
                    NetCalculationRuleKeys.calculateNet(
                        rule = rule,
                        correctCount =
                            record.correctCount,
                        wrongCount =
                            record.wrongCount
                    )
                } / records.size.toDouble()
            }
        }.stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = 0.0
        )

    val studyFrequencyPerWeek:
            StateFlow<Double> =
        recordsForSubject
            .map { records ->
                calculateFrequencyPerWeek(
                    records
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = 0.0
            )

    val lastStudyDateMillis:
            StateFlow<Long?> =
        recordsForSubject
            .map { records ->
                records.maxOfOrNull { record ->
                    record.recordDateMillis
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = null
            )

    private fun calculateFrequencyPerWeek(
        records: List<StudyRecordEntity>
    ): Double {
        if (records.isEmpty()) {
            return 0.0
        }

        val earliest =
            records.minOf { record ->
                record.recordDateMillis
            }

        val latest =
            records.maxOf { record ->
                record.recordDateMillis
            }

        val spanMillis =
            (latest - earliest)
                .coerceAtLeast(0L)

        val weeks =
            (
                    spanMillis.toDouble() /
                            ONE_WEEK_MILLIS.toDouble()
                    ).coerceAtLeast(1.0)

        return records.size.toDouble() / weeks
    }
}
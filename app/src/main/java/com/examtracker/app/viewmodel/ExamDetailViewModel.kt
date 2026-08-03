package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.ExamEntity
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.data.local.StudyEntryTypeKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val RECENT_STUDY_RECORDS_LIMIT = 20
private const val STATE_FLOW_STOP_TIMEOUT_MILLIS = 5_000L

sealed interface ExamDetailEvent {
    data object SubjectHasRecordsCannotDelete : ExamDetailEvent
}

class ExamDetailViewModel(
    private val examId: Long,
    private val examRepository: ExamRepository,
    private val subjectRepository: SubjectRepository,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModel() {

    private val eventChannel = Channel<ExamDetailEvent>(Channel.BUFFERED)
    val events: Flow<ExamDetailEvent> = eventChannel.receiveAsFlow()

    val exam: StateFlow<ExamEntity?> =
        examRepository.getExamById(examId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = null
            )

    val subjects: StateFlow<List<SubjectEntity>> =
        subjectRepository.getSubjectsForExam(examId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = emptyList()
            )

    val studyRecords: StateFlow<List<StudyRecordEntity>> =
        studyRecordRepository.getStudyRecordsForExam(examId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = emptyList()
            )

    val recentStudyRecords: StateFlow<List<StudyRecordEntity>> =
        studyRecordRepository.getRecentStudyRecordsForExam(
            examId = examId,
            limit = RECENT_STUDY_RECORDS_LIMIT
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                STATE_FLOW_STOP_TIMEOUT_MILLIS
            ),
            initialValue = emptyList()
        )

    val totalStudyMinutes: StateFlow<Int> =
        studyRecords
            .map { records ->
                records.sumOf { record -> record.durationMinutes }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = 0
            )

    val totalCorrect: StateFlow<Int> =
        studyRecords
            .map { records ->
                records.sumOf { record -> record.correctCount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = 0
            )

    val totalWrong: StateFlow<Int> =
        studyRecords
            .map { records ->
                records.sumOf { record -> record.wrongCount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = 0
            )

    val totalBlank: StateFlow<Int> =
        studyRecords
            .map { records ->
                records.sumOf { record -> record.blankCount }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = 0
            )

    val totalSolvedQuestions: StateFlow<Int> =
        studyRecords
            .map { records ->
                records.sumOf { record ->
                    record.correctCount +
                            record.wrongCount +
                            record.blankCount
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STATE_FLOW_STOP_TIMEOUT_MILLIS
                ),
                initialValue = 0
            )

    val totalNet: StateFlow<Double> =
        combine(exam, studyRecords) { examEntity, records ->
            val correctCount = records.sumOf { record ->
                record.correctCount
            }

            val wrongCount = records.sumOf { record ->
                record.wrongCount
            }

            NetCalculationRuleKeys.calculateNet(
                rule = examEntity?.netCalculationRule,
                correctCount = correctCount,
                wrongCount = wrongCount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                STATE_FLOW_STOP_TIMEOUT_MILLIS
            ),
            initialValue = 0.0
        )

    fun addSubject(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return
        }

        viewModelScope.launch {
            val subject = SubjectEntity(
                examId = examId,
                name = trimmedName,
                createdAtMillis = System.currentTimeMillis()
            )

            subjectRepository.insertSubject(subject)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            val recordCount =
                subjectRepository.getStudyRecordCountForSubject(subject.id)

            if (recordCount > 0) {
                eventChannel.send(
                    ExamDetailEvent.SubjectHasRecordsCannotDelete
                )
            } else {
                subjectRepository.deleteSubject(subject)
            }
        }
    }

    fun addManualStudyRecord(
        subjectId: Long,
        durationMinutes: Int,
        correctCount: Int,
        wrongCount: Int,
        blankCount: Int,
        recordDateMillis: Long,
        note: String?
    ) {
        if (
            durationMinutes < 0 ||
            correctCount < 0 ||
            wrongCount < 0 ||
            blankCount < 0
        ) {
            return
        }

        val subjectExists = subjects.value.any { subject ->
            subject.id == subjectId
        }

        if (!subjectExists) {
            return
        }

        val normalizedNote = note
            ?.trim()
            ?.takeIf { trimmedNote -> trimmedNote.isNotEmpty() }

        viewModelScope.launch {
            val record = StudyRecordEntity(
                examId = examId,
                subjectId = subjectId,
                durationMinutes = durationMinutes,
                correctCount = correctCount,
                wrongCount = wrongCount,
                blankCount = blankCount,
                recordDateMillis = recordDateMillis,
                note = normalizedNote,
                entryType = StudyEntryTypeKeys.MANUAL,
                createdAtMillis = System.currentTimeMillis()
            )

            studyRecordRepository.insertStudyRecord(record)
        }
    }

    fun deleteStudyRecord(record: StudyRecordEntity) {
        viewModelScope.launch {
            studyRecordRepository.deleteStudyRecord(record)
        }
    }
}
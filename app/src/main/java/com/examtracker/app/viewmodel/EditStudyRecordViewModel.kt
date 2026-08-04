package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.repository.StudyRecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val EDIT_RECORD_STATE_TIMEOUT_MILLIS = 5_000L

class EditStudyRecordViewModel(
    private val recordId: Long,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModel() {

    val record: StateFlow<StudyRecordEntity?> =
        studyRecordRepository
            .getStudyRecordById(recordId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    EDIT_RECORD_STATE_TIMEOUT_MILLIS
                ),
                initialValue = null
            )

    fun updateRecord(
        durationMinutes: Int,
        correctCount: Int,
        wrongCount: Int,
        blankCount: Int,
        recordDateMillis: Long,
        note: String?,
        onComplete: () -> Unit
    ) {
        if (
            durationMinutes < 0 ||
            correctCount < 0 ||
            wrongCount < 0 ||
            blankCount < 0
        ) {
            return
        }

        val currentRecord =
            record.value ?: return

        val normalizedNote =
            note
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

        viewModelScope.launch {
            val updatedRecord =
                currentRecord.copy(
                    durationMinutes =
                        durationMinutes,
                    correctCount =
                        correctCount,
                    wrongCount =
                        wrongCount,
                    blankCount =
                        blankCount,
                    recordDateMillis =
                        recordDateMillis,
                    note =
                        normalizedNote
                )

            studyRecordRepository
                .updateStudyRecord(
                    updatedRecord
                )

            onComplete()
        }
    }
}
package com.examtracker.app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.examtracker.app.data.repository.StudyRecordRepository

class EditStudyRecordViewModelFactory(
    private val recordId: Long,
    private val studyRecordRepository:
    StudyRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                EditStudyRecordViewModel::class.java
            )
        ) {
            return EditStudyRecordViewModel(
                recordId = recordId,
                studyRecordRepository =
                    studyRecordRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
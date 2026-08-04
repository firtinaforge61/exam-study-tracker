package com.examtracker.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository

class TimerViewModelFactory(
    private val examId: Long,
    private val subjectId: Long,
    private val config: TimerConfig,
    private val examRepository: ExamRepository,
    private val subjectRepository: SubjectRepository,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (
            modelClass.isAssignableFrom(
                TimerViewModel::class.java
            )
        ) {
            val savedStateHandle: SavedStateHandle =
                extras.createSavedStateHandle()

            return TimerViewModel(
                examId = examId,
                subjectId = subjectId,
                config = config,
                examRepository = examRepository,
                subjectRepository = subjectRepository,
                studyRecordRepository = studyRecordRepository,
                savedStateHandle = savedStateHandle
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
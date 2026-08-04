package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository

class SubjectStatisticsViewModelFactory(
    private val subjectId: Long,
    private val subjectRepository: SubjectRepository,
    private val examRepository: ExamRepository,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                SubjectStatisticsViewModel::class.java
            )
        ) {
            return SubjectStatisticsViewModel(
                subjectId = subjectId,
                subjectRepository = subjectRepository,
                examRepository = examRepository,
                studyRecordRepository = studyRecordRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
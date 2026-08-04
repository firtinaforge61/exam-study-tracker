package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository

class DashboardViewModelFactory(
    private val examRepository: ExamRepository,
    private val studyRecordRepository: StudyRecordRepository,
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                DashboardViewModel::class.java
            )
        ) {
            return DashboardViewModel(
                examRepository = examRepository,
                studyRecordRepository =
                    studyRecordRepository,
                subjectRepository = subjectRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
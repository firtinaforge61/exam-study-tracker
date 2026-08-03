package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository

class ExamDetailViewModelFactory(
    private val examId: Long,
    private val examRepository: ExamRepository,
    private val subjectRepository: SubjectRepository,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamDetailViewModel::class.java)) {
            return ExamDetailViewModel(
                examId = examId,
                examRepository = examRepository,
                subjectRepository = subjectRepository,
                studyRecordRepository = studyRecordRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.ExamEntity
import com.examtracker.app.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamViewModel(
    private val repository: ExamRepository
) : ViewModel() {

    private val _exams = MutableStateFlow<List<ExamEntity>>(emptyList())
    val exams: StateFlow<List<ExamEntity>> = _exams.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllExams().collect { examList ->
                _exams.value = examList
            }
        }
    }

    fun createExam(
        examName: String,
        examDateMillis: Long?,
        dailyQuestionGoal: Int,
        netCalculationRule: String
    ) {
        viewModelScope.launch {
            val exam = ExamEntity(
                name = examName.trim(),
                examDateMillis = examDateMillis,
                dailyQuestionGoal = dailyQuestionGoal,
                netCalculationRule = netCalculationRule,
                createdAtMillis = System.currentTimeMillis()
            )

            repository.insertExam(exam)
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }
}
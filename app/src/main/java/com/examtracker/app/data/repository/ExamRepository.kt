package com.examtracker.app.data.repository

import com.examtracker.app.data.local.ExamDao
import com.examtracker.app.data.local.ExamEntity
import kotlinx.coroutines.flow.Flow

class ExamRepository(
    private val examDao: ExamDao
) {
    suspend fun insertExam(exam: ExamEntity): Long = examDao.insertExam(exam)

    fun getAllExams(): Flow<List<ExamEntity>> = examDao.getAllExams()

    fun getExamById(examId: Long): Flow<ExamEntity?> = examDao.getExamById(examId)

    suspend fun updateExam(exam: ExamEntity) = examDao.updateExam(exam)

    suspend fun deleteExam(exam: ExamEntity) = examDao.deleteExam(exam)
}
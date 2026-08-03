package com.examtracker.app.data.repository

import com.examtracker.app.data.local.SubjectDao
import com.examtracker.app.data.local.SubjectEntity
import kotlinx.coroutines.flow.Flow

class SubjectRepository(
    private val subjectDao: SubjectDao
) {
    suspend fun insertSubject(subject: SubjectEntity): Long = subjectDao.insertSubject(subject)

    fun getSubjectsForExam(examId: Long): Flow<List<SubjectEntity>> =
        subjectDao.getSubjectsForExam(examId)

    suspend fun deleteSubject(subject: SubjectEntity) = subjectDao.deleteSubject(subject)

    suspend fun getStudyRecordCountForSubject(subjectId: Long): Int =
        subjectDao.getStudyRecordCountForSubject(subjectId)
}
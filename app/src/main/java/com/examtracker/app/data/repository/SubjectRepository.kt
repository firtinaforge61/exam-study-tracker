package com.examtracker.app.data.repository

import com.examtracker.app.data.local.SubjectDao
import com.examtracker.app.data.local.SubjectEntity
import kotlinx.coroutines.flow.Flow

class SubjectRepository(
    private val subjectDao: SubjectDao
) {

    suspend fun insertSubject(
        subject: SubjectEntity
    ): Long {
        return subjectDao.insertSubject(subject)
    }

    fun getSubjectsForExam(
        examId: Long
    ): Flow<List<SubjectEntity>> {
        return subjectDao.getSubjectsForExam(examId)
    }

    fun getAllSubjects(): Flow<List<SubjectEntity>> {
        return subjectDao.getAllSubjects()
    }

    fun getSubjectById(
        id: Long
    ): Flow<SubjectEntity?> {
        return subjectDao.getSubjectById(id)
    }

    suspend fun deleteSubject(
        subject: SubjectEntity
    ) {
        subjectDao.deleteSubject(subject)
    }

    suspend fun getStudyRecordCountForSubject(
        subjectId: Long
    ): Int {
        return subjectDao.getStudyRecordCountForSubject(subjectId)
    }
}
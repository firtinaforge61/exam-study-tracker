package com.examtracker.app.data.repository

import com.examtracker.app.data.local.StudyRecordDao
import com.examtracker.app.data.local.StudyRecordEntity
import kotlinx.coroutines.flow.Flow

class StudyRecordRepository(
    private val studyRecordDao: StudyRecordDao
) {

    suspend fun insertStudyRecord(
        record: StudyRecordEntity
    ): Long {
        return studyRecordDao.insertStudyRecord(record)
    }

    suspend fun updateStudyRecord(
        record: StudyRecordEntity
    ) {
        studyRecordDao.updateStudyRecord(record)
    }

    fun getStudyRecordsForExam(
        examId: Long
    ): Flow<List<StudyRecordEntity>> {
        return studyRecordDao.getStudyRecordsForExam(examId)
    }

    fun getRecentStudyRecordsForExam(
        examId: Long,
        limit: Int
    ): Flow<List<StudyRecordEntity>> {
        return studyRecordDao.getRecentStudyRecordsForExam(
            examId = examId,
            limit = limit
        )
    }

    fun getStudyRecordById(
        id: Long
    ): Flow<StudyRecordEntity?> {
        return studyRecordDao.getStudyRecordById(id)
    }

    fun getAllStudyRecords(): Flow<List<StudyRecordEntity>> {
        return studyRecordDao.getAllStudyRecords()
    }

    suspend fun deleteStudyRecord(
        record: StudyRecordEntity
    ) {
        studyRecordDao.deleteStudyRecord(record)
    }
}
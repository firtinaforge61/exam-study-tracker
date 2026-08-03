package com.examtracker.app.data.repository

import com.examtracker.app.data.local.StudyRecordDao
import com.examtracker.app.data.local.StudyRecordEntity
import kotlinx.coroutines.flow.Flow

class StudyRecordRepository(
    private val studyRecordDao: StudyRecordDao
) {
    suspend fun insertStudyRecord(record: StudyRecordEntity): Long =
        studyRecordDao.insertStudyRecord(record)

    fun getStudyRecordsForExam(examId: Long): Flow<List<StudyRecordEntity>> =
        studyRecordDao.getStudyRecordsForExam(examId)

    fun getRecentStudyRecordsForExam(examId: Long, limit: Int): Flow<List<StudyRecordEntity>> =
        studyRecordDao.getRecentStudyRecordsForExam(examId, limit)

    suspend fun deleteStudyRecord(record: StudyRecordEntity) =
        studyRecordDao.deleteStudyRecord(record)
}
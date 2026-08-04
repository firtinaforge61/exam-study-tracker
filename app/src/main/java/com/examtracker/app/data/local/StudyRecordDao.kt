package com.examtracker.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRecordDao {

    @Insert
    suspend fun insertStudyRecord(record: StudyRecordEntity): Long

    @Update
    suspend fun updateStudyRecord(record: StudyRecordEntity)

    @Query(
        "SELECT * FROM study_records " +
                "WHERE examId = :examId " +
                "ORDER BY recordDateMillis DESC"
    )
    fun getStudyRecordsForExam(
        examId: Long
    ): Flow<List<StudyRecordEntity>>

    @Query(
        "SELECT * FROM study_records " +
                "WHERE examId = :examId " +
                "ORDER BY recordDateMillis DESC " +
                "LIMIT :limit"
    )
    fun getRecentStudyRecordsForExam(
        examId: Long,
        limit: Int
    ): Flow<List<StudyRecordEntity>>

    @Query("SELECT * FROM study_records WHERE id = :id")
    fun getStudyRecordById(
        id: Long
    ): Flow<StudyRecordEntity?>

    @Query(
        "SELECT * FROM study_records " +
                "ORDER BY recordDateMillis DESC"
    )
    fun getAllStudyRecords(): Flow<List<StudyRecordEntity>>

    @Delete
    suspend fun deleteStudyRecord(record: StudyRecordEntity)
}
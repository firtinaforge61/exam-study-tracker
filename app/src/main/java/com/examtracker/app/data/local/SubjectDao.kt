package com.examtracker.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Query("SELECT * FROM subjects WHERE examId = :examId ORDER BY createdAtMillis ASC")
    fun getSubjectsForExam(examId: Long): Flow<List<SubjectEntity>>

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT COUNT(*) FROM study_records WHERE subjectId = :subjectId")
    suspend fun getStudyRecordCountForSubject(subjectId: Long): Int
}
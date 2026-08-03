package com.examtracker.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_records",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["examId"]),
        Index(value = ["subjectId"]),
        Index(value = ["recordDateMillis"])
    ]
)
data class StudyRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val examId: Long,
    val subjectId: Long,
    val durationMinutes: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val blankCount: Int,
    val recordDateMillis: Long,
    val note: String?,
    val entryType: String,
    val createdAtMillis: Long
)
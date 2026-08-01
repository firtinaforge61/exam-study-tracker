package com.examtracker.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val examDateMillis: Long?,
    val dailyQuestionGoal: Int,
    val netCalculationRule: String,
    val createdAtMillis: Long
)
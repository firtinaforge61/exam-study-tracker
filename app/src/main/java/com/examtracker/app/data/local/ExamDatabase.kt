package com.examtracker.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExamEntity::class,
        SubjectEntity::class,
        StudyRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ExamDatabase : RoomDatabase() {

    abstract fun examDao(): ExamDao
    abstract fun subjectDao(): SubjectDao
    abstract fun studyRecordDao(): StudyRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ExamDatabase? = null

        fun getInstance(context: Context): ExamDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ExamDatabase::class.java,
                    "exam_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
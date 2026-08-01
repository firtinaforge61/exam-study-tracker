package com.examtracker.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ExamEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExamDatabase : RoomDatabase() {

    abstract fun examDao(): ExamDao

    companion object {
        @Volatile
        private var INSTANCE: ExamDatabase? = null

        fun getInstance(context: Context): ExamDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ExamDatabase::class.java,
                    "exam_tracker_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
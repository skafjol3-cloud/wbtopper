package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TestAttemptEntity::class,
        BookmarkEntity::class,
        SupportTicketEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BanglaExamDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao

    companion object {
        @Volatile
        private var INSTANCE: BanglaExamDatabase? = null

        fun getDatabase(context: Context): BanglaExamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BanglaExamDatabase::class.java,
                    "bangla_exam_preparation_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

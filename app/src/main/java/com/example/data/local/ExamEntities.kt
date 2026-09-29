package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val testTitle: String,
    val score: Double,
    val maxMarks: Double,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val timeSpentSeconds: Int,
    val completedAt: Long
)

@Entity(tableName = "saved_bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // QUESTION, STUDY_MATERIAL, COURSE
    val subtitle: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val studentEmail: String,
    val subject: String,
    val category: String,
    val message: String,
    val status: String,
    val reply: String?,
    val createdAt: String
)

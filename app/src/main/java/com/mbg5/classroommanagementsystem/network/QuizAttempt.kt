// QuizAttempt.kt
package com.mbg5.classroommanagementsystem.network

data class QuizAttempt(
    val id: String,
    val quizId: String,
    val studentId: String,
    val answers: Map<String, String>,
    val score: Int,
    val timestamp: Long
)
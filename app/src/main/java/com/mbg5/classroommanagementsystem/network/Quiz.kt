// Quiz.kt
package com.mbg5.classroommanagementsystem.network

data class Quiz(
    val id: String,
    val classId: String,
    val title: String,
    val questions: List<Question>
)

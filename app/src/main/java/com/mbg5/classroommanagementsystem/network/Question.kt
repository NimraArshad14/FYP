package com.mbg5.classroommanagementsystem.network

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)
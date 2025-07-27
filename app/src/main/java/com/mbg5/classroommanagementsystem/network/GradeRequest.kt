package com.mbg5.classroommanagementsystem.network

data class GradeRequest(
    val studentId: String,
    val value: String,
    val comment: String? = null
)

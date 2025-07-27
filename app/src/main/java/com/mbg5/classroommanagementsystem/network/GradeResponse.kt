package com.mbg5.classroommanagementsystem.network

data class GradeResponse(
    val id: String,
    val classId: String,
    val studentId: String,
    val teacherId: String,
    val value: String,
    val comment: String?,
    val timestamp: Long
)

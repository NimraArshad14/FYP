// TeacherRequest.kt
package com.mbg5.classroommanagementsystem.network

data class TeacherRequest(
    val fullName: String,
    val subjectExpertise: String,
    val qualification: String,
    val yearsOfExperience: Int,
    val email: String,
    val phone: String,
    val password: String
)

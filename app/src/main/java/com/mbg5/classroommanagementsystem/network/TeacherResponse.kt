// TeacherResponse.kt
package com.mbg5.classroommanagementsystem.network

data class TeacherResponse(
    val id: String,
    val fullName: String,
    val subjectExpertise: String,
    val qualification: String,
    val yearsOfExperience: Int,
    val email: String,
    val phone: String,
    val role: String
)

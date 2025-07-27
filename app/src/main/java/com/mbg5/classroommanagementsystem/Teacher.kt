package com.mbg5.classroommanagementsystem

data class Teacher(
    val uid: String,
    val fullName: String,
    val subjectExpertise: String,
    val qualification: String,
    val yearsOfExperience: Int,
    val email: String,
    val phone: String
)

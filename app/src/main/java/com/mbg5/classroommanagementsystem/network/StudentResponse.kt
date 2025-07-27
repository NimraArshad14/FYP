// StudentResponse.kt
package com.mbg5.classroommanagementsystem.network

data class StudentResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val clazz: String,
    val imageUrl: String,
    val role: String
)

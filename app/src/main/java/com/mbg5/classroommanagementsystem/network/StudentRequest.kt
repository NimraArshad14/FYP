// StudentRequest.kt
package com.mbg5.classroommanagementsystem.network

data class StudentRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String,
    val clazz: String
)

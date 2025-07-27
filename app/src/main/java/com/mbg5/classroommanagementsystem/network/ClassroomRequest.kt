// ClassroomRequest.kt
package com.mbg5.classroommanagementsystem.network

data class ClassroomRequest(
    val name: String,
    val teacherId: String,
    val studentIds: List<String>
)

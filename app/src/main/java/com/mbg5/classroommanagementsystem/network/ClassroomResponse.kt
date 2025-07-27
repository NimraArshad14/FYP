// File: app/src/main/java/com/mbg5/classroommanagementsystem/network/ClassroomResponse.kt
package com.mbg5.classroommanagementsystem.network

data class ClassroomResponse(
    val id: String,
    val name: String,
    // these exactly match the JSON your backend produces
    val teacher: TeacherProfileResponse,
    val students: List<StudentProfileResponse>
)

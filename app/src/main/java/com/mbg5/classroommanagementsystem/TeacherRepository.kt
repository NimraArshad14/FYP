// File: app/src/main/java/com/mbg5/classroommanagementsystem/TeacherRepository.kt
package com.mbg5.classroommanagementsystem

import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.TeacherRequest
import com.mbg5.classroommanagementsystem.network.TeacherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeacherRepository {
    suspend fun registerTeacher(
        fullName: String,
        subjectExpertise: String,
        qualification: String,
        yearsOfExperience: Int,
        email: String,
        phone: String,
        password: String
    ): TeacherResponse = withContext(Dispatchers.IO) {
        val req = TeacherRequest(
            fullName,
            subjectExpertise,
            qualification,
            yearsOfExperience,
            email,
            phone,
            password
        )
        // <-- use createTeacher() here, not registerTeacher()
        val resp = ApiClient.apiService.createTeacher(req)
        if (!resp.isSuccessful) throw Exception("Failed to register: ${resp.code()}")
        resp.body()!!
    }

    suspend fun getTeacher(id: String): TeacherResponse = withContext(Dispatchers.IO) {
        val resp = ApiClient.apiService.getTeacher(id)
        if (!resp.isSuccessful) throw Exception("Failed to load profile: ${resp.code()}")
        resp.body()!!
    }
}

package com.mbg5.classroommanagementsystem.network

// import com.google.firebase.Timestamp // No longer needed
import com.google.gson.annotations.SerializedName

// All date fields are now String to match backend ISO8601 string format

data class LeaveResponse(
    val id: String,
    val studentId: String,
    val studentName: String,
    val studentClass: String,
    val leaveType: String,
    val reason: String,
    val aiGeneratedApplication: String,
    val startDate: String, // was Timestamp, now String
    val endDate: String,   // was Timestamp, now String
    val numberOfDays: Int,
    val status: String,
    val teacherId: String,
    val teacherName: String,
    val teacherResponse: String?,
    @SerializedName("createdAt")
    val createdAt: String, // was Timestamp, now String
    @SerializedName("updatedAt")
    val updatedAt: String  // was Timestamp, now String
) 
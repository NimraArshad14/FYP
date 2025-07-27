package com.mbg5.classroommanagementsystem.network

data class ScheduleResponse(
    val id: String,
    val url: String, // URL to the uploaded file (PDF/image)
    val uploadedAt: Long
) 
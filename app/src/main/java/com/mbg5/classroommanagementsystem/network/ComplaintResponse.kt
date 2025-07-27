package com.mbg5.classroommanagementsystem.network

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ComplaintResponse(
    val id: String,
    val studentId: String,
    val studentName: String,
    val studentClass: String,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val adminResponse: String?,
    val priority: String,
    @SerializedName("createdAt")
    val createdAt: Date,
    @SerializedName("updatedAt")
    val updatedAt: Date
) 
package com.mbg5.classroommanagementsystem.network

import java.util.Date

data class FeeResponse(
    val id: String,
    val studentId: String,
    val studentName: String,
    val studentEmail: String,
    val studentClass: String,
    val amount: Double,
    val isPaid: Boolean,
    val isVerified: Boolean,
    val dueDate: Date,
    val paidDate: Date?,
    val createdAt: Date,
    val paymentMethod: String?,
    val receiptNumber: String?,
    val notes: String?
) 
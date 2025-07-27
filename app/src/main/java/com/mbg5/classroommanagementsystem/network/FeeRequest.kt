package com.mbg5.classroommanagementsystem.network

data class FeeRequest(
    val studentId: String,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean = false,
    val isVerified: Boolean = false,
    val paymentMethod: String? = null,
    val receiptNumber: String? = null,
    val notes: String? = null
) 
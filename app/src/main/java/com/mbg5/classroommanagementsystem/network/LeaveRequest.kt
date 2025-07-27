package com.mbg5.classroommanagementsystem.network

import java.util.Date

data class LeaveRequest(
    val leaveType: String,
    val reason: String,
    val startDate: String,
    val endDate: String,
    val numberOfDays: Int,
    val teacherId: String
) 
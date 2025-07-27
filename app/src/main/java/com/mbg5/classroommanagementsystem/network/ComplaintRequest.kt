package com.mbg5.classroommanagementsystem.network

data class ComplaintRequest(
    val title: String,
    val description: String,
    val category: String,
    val priority: String
) 
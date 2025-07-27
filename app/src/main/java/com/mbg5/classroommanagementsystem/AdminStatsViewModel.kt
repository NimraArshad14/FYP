package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class to hold stats
data class AdminStats(
    val studentCount: Int = 0,
    val teacherCount: Int = 0,
    val classCount: Int = 0
)

class AdminStatsViewModel : ViewModel() {
    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchStats() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val students = ApiClient.apiService.listStudents().body()?.size ?: 0
                val teachers = ApiClient.apiService.listTeachers().body()?.size ?: 0
                val classes = ApiClient.apiService.listClasses().body()?.size ?: 0
                _stats.value = AdminStats(students, teachers, classes)
            } catch (e: Exception) {
                _stats.value = AdminStats(0, 0, 0)
            }
            _loading.value = false
        }
    }
} 
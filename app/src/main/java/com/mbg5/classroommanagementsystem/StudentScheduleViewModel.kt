package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.ScheduleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentScheduleViewModel : ViewModel() {
    private val _schedule = MutableStateFlow<ScheduleResponse?>(null)
    val schedule: StateFlow<ScheduleResponse?> = _schedule

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchLatestSchedule() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = ApiClient.apiService.getLatestSchedule()
                if (response.isSuccessful) {
                    _schedule.value = response.body()
                } else {
                    _error.value = "Failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _loading.value = false
        }
    }
} 
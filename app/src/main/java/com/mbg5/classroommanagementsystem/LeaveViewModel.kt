package com.mbg5.classroommanagementsystem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.LeaveRequest
import com.mbg5.classroommanagementsystem.network.LeaveResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat

class LeaveViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _leaves = MutableStateFlow<List<LeaveResponse>>(emptyList())
    val leaves: StateFlow<List<LeaveResponse>> = _leaves

    private val _pendingLeaves = MutableStateFlow<List<LeaveResponse>>(emptyList())
    val pendingLeaves: StateFlow<List<LeaveResponse>> = _pendingLeaves

    private val _approvedLeaves = MutableStateFlow<List<LeaveResponse>>(emptyList())
    val approvedLeaves: StateFlow<List<LeaveResponse>> = _approvedLeaves

    private val _rejectedLeaves = MutableStateFlow<List<LeaveResponse>>(emptyList())
    val rejectedLeaves: StateFlow<List<LeaveResponse>> = _rejectedLeaves

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun createLeave(
        leaveType: String,
        reason: String,
        startDate: Date,
        endDate: Date,
        numberOfDays: Int,
        teacherId: String,
        studentId: String,
        studentName: String,
        studentClass: String,
        teacherName: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("LeaveViewModel", "Creating leave application: type=$leaveType, reason=$reason, days=$numberOfDays")
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val startDateString = dateFormat.format(startDate)
                val endDateString = dateFormat.format(endDate)
                val request = com.mbg5.classroommanagementsystem.network.LeaveRequest(leaveType, reason, startDateString, endDateString, numberOfDays, teacherId)
                val response = apiService.createLeave(request, studentId, studentName, studentClass, teacherName)
                
                _success.value = "Leave application submitted successfully!"
                fetchLeavesByStudent(studentId)
            } catch (e: Exception) {
                Log.e("LeaveViewModel", "Error creating leave: ${e.message}", e)
                _error.value = "Failed to submit leave application: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchLeavesByStudent(studentId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("LeaveViewModel", "Fetching leaves for student: $studentId")
                val response = apiService.getLeavesByStudent(studentId)
                _leaves.value = response
                
                // Update filtered leaves
                _pendingLeaves.value = response.filter { it.status == "PENDING" }
                _approvedLeaves.value = response.filter { it.status == "APPROVED" }
                _rejectedLeaves.value = response.filter { it.status == "REJECTED" }
                
                Log.d("LeaveViewModel", "Found ${response.size} leaves for student")
            } catch (e: Exception) {
                Log.e("LeaveViewModel", "Error fetching leaves: ${e.message}", e)
                _error.value = "Failed to fetch leave applications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchLeavesByTeacher(teacherId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("LeaveViewModel", "Fetching leaves for teacher: $teacherId")
                val response = apiService.getLeavesByTeacher(teacherId)
                _leaves.value = response
                
                // Update filtered leaves
                _pendingLeaves.value = response.filter { it.status == "PENDING" }
                _approvedLeaves.value = response.filter { it.status == "APPROVED" }
                _rejectedLeaves.value = response.filter { it.status == "REJECTED" }
                
                Log.d("LeaveViewModel", "Found ${response.size} leaves for teacher")
            } catch (e: Exception) {
                Log.e("LeaveViewModel", "Error fetching leaves: ${e.message}", e)
                _error.value = "Failed to fetch leave applications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateLeaveStatus(leaveId: String, status: String, teacherResponse: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("LeaveViewModel", "Updating leave status: $leaveId to $status")
                val response = apiService.updateLeaveStatus(leaveId, status, teacherResponse)
                
                _success.value = "Leave application $status successfully!"
                
                // Refresh the list
                val currentLeaves = _leaves.value
                val updatedLeaves = currentLeaves.map { 
                    if (it.id == leaveId) response else it 
                }
                _leaves.value = updatedLeaves
                
                // Update filtered lists
                _pendingLeaves.value = updatedLeaves.filter { it.status == "PENDING" }
                _approvedLeaves.value = updatedLeaves.filter { it.status == "APPROVED" }
                _rejectedLeaves.value = updatedLeaves.filter { it.status == "REJECTED" }
                
            } catch (e: Exception) {
                Log.e("LeaveViewModel", "Error updating leave status: ${e.message}", e)
                _error.value = "Failed to update leave application: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteLeave(leaveId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("LeaveViewModel", "Deleting leave: $leaveId")
                apiService.deleteLeave(leaveId)
                
                _success.value = "Leave application deleted successfully!"
                
                // Remove from lists
                val currentLeaves = _leaves.value.filter { it.id != leaveId }
                _leaves.value = currentLeaves
                
                // Update filtered lists
                _pendingLeaves.value = currentLeaves.filter { it.status == "PENDING" }
                _approvedLeaves.value = currentLeaves.filter { it.status == "APPROVED" }
                _rejectedLeaves.value = currentLeaves.filter { it.status == "REJECTED" }
                
            } catch (e: Exception) {
                Log.e("LeaveViewModel", "Error deleting leave: ${e.message}", e)
                _error.value = "Failed to delete leave application: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
} 
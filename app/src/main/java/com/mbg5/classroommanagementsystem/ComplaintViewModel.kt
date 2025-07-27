package com.mbg5.classroommanagementsystem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.ComplaintRequest
import com.mbg5.classroommanagementsystem.network.ComplaintResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComplaintViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _complaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val complaints: StateFlow<List<ComplaintResponse>> = _complaints

    // Separate StateFlows for filtered complaints
    private val _pendingComplaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val pendingComplaints: StateFlow<List<ComplaintResponse>> = _pendingComplaints

    private val _resolvedComplaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val resolvedComplaints: StateFlow<List<ComplaintResponse>> = _resolvedComplaints

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun createComplaint(
        title: String,
        description: String,
        category: String,
        priority: String,
        studentId: String,
        studentName: String,
        studentClass: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                val request = ComplaintRequest(title, description, category, priority)
                val response = apiService.createComplaint(request, studentId, studentName, studentClass)
                
                _success.value = "Complaint submitted successfully!"
                fetchComplaints(studentId)
            } catch (e: Exception) {
                _error.value = "Failed to submit complaint: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchComplaints(studentId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                Log.d("ComplaintViewModel", "Fetching complaints for student ID: $studentId")
                val response = apiService.getComplaintsByStudent(studentId)
                Log.d("ComplaintViewModel", "Successfully fetched ${response.size} complaints")
                _complaints.value = response
                
                // Update filtered complaints
                _pendingComplaints.value = response.filter { 
                    it.status == "PENDING" || it.status == "IN_PROGRESS" 
                }
                _resolvedComplaints.value = response.filter { 
                    it.status == "RESOLVED" 
                }
            } catch (e: Exception) {
                Log.e("ComplaintViewModel", "Error fetching complaints: ${e.message}", e)
                _error.value = "Failed to fetch complaints: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Keep the old method for backward compatibility
    fun fetchComplaintsByStudent(studentId: String) {
        fetchComplaints(studentId)
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
} 
package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.ComplaintResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminComplaintViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _complaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val complaints: StateFlow<List<ComplaintResponse>> = _complaints

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    private val _pendingComplaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val pendingComplaints: StateFlow<List<ComplaintResponse>> = _pendingComplaints

    private val _resolvedComplaints = MutableStateFlow<List<ComplaintResponse>>(emptyList())
    val resolvedComplaints: StateFlow<List<ComplaintResponse>> = _resolvedComplaints

    fun fetchAllComplaints() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                val response = apiService.getAllComplaints()
                _complaints.value = response
                
                // Filter complaints by status
                _pendingComplaints.value = response.filter { 
                    it.status == "PENDING" || it.status == "IN_PROGRESS" 
                }
                _resolvedComplaints.value = response.filter { 
                    it.status == "RESOLVED" || it.status == "REJECTED" 
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch complaints: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateComplaintStatus(complaintId: String, status: String, adminResponse: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                val response = apiService.updateComplaintStatus(complaintId, status, adminResponse)
                
                _success.value = "Complaint status updated successfully!"
                fetchAllComplaints() // Refresh the list
            } catch (e: Exception) {
                _error.value = "Failed to update complaint: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteComplaint(complaintId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                apiService.deleteComplaint(complaintId)
                
                _success.value = "Complaint deleted successfully!"
                fetchAllComplaints() // Refresh the list
            } catch (e: Exception) {
                _error.value = "Failed to delete complaint: ${e.message}"
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
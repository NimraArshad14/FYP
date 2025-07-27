package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.FeeRequest
import com.mbg5.classroommanagementsystem.network.FeeResponse
import com.mbg5.classroommanagementsystem.network.StudentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Date.toIsoString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(this)
}

class FeeManagementViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _fees = MutableStateFlow<List<FeeResponse>>(emptyList())
    val fees: StateFlow<List<FeeResponse>> = _fees.asStateFlow()

    private val _students = MutableStateFlow<List<StudentResponse>>(emptyList())
    val students: StateFlow<List<StudentResponse>> = _students.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _unpaidFees = MutableStateFlow<List<FeeResponse>>(emptyList())
    val unpaidFees: StateFlow<List<FeeResponse>> = _unpaidFees.asStateFlow()

    private val _unverifiedFees = MutableStateFlow<List<FeeResponse>>(emptyList())
    val unverifiedFees: StateFlow<List<FeeResponse>> = _unverifiedFees.asStateFlow()

    init {
        fetchAllFees()
        fetchAllStudents()
    }

    fun fetchAllFees() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = apiService.listFees()
                if (response.isSuccessful) {
                    _fees.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch fees: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchAllStudents() {
        viewModelScope.launch {
            try {
                val response = apiService.listStudents()
                if (response.isSuccessful) {
                    _students.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle silently for now
            }
        }
    }

    fun fetchUnpaidFees() {
        viewModelScope.launch {
            try {
                val response = apiService.getUnpaidFees()
                if (response.isSuccessful) {
                    _unpaidFees.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Error fetching unpaid fees: ${e.message}"
            }
        }
    }

    fun fetchUnverifiedFees() {
        viewModelScope.launch {
            try {
                val response = apiService.getUnverifiedFees()
                if (response.isSuccessful) {
                    _unverifiedFees.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Error fetching unverified fees: ${e.message}"
            }
        }
    }

    fun createFee(studentId: String, amount: Double, dueDate: Date, notes: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val request = FeeRequest(
                    studentId = studentId,
                    amount = amount,
                    dueDate = dueDate.toIsoString(),
                    notes = notes
                )
                val response = apiService.createFee(request)
                if (response.isSuccessful) {
                    fetchAllFees()
                } else {
                    _error.value = "Failed to create fee: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAsPaid(feeId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.markFeeAsPaid(feeId)
                if (response.isSuccessful) {
                    fetchAllFees()
                    fetchUnpaidFees()
                } else {
                    _error.value = "Failed to mark as paid: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun markAsVerified(feeId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.markFeeAsVerified(feeId)
                if (response.isSuccessful) {
                    fetchAllFees()
                    fetchUnverifiedFees()
                } else {
                    _error.value = "Failed to mark as verified: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun updateFee(feeId: String, request: FeeRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = apiService.updateFee(feeId, request)
                if (response.isSuccessful) {
                    fetchAllFees()
                } else {
                    _error.value = "Failed to update fee: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteFee(feeId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteFee(feeId)
                if (response.isSuccessful) {
                    fetchAllFees()
                } else {
                    _error.value = "Failed to delete fee: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun getStudentById(studentId: String): StudentResponse? {
        return students.value.find { it.id == studentId }
    }

    fun clearError() {
        _error.value = null
    }
} 
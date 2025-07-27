package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class TeacherState {
    object Idle : TeacherState()
    object Loading : TeacherState()
    data class DetailLoaded(val teacher: Teacher) : TeacherState()
    object Registered : TeacherState()
    data class Error(val message: String) : TeacherState()
}

class TeacherViewModel(
    private val repo: TeacherRepository = TeacherRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<TeacherState>(TeacherState.Idle)
    val state: StateFlow<TeacherState> = _state

    fun reset() {
        _state.value = TeacherState.Idle
    }

    /** CREATE */
    fun registerTeacher(
        fullName: String,
        subjectExpertise: String,
        qualification: String,
        yearsOfExperience: Int,
        email: String,
        phone: String,
        password: String
    ) {
        if (fullName.isBlank() || email.isBlank() || password.length < 8) {
            _state.value = TeacherState.Error("All fields required; password ≥8 chars.")
            return
        }
        _state.value = TeacherState.Loading
        viewModelScope.launch {
            try {
                // we don’t need the network’s TeacherResponse here, only signal Registered
                repo.registerTeacher(
                    fullName, subjectExpertise, qualification,
                    yearsOfExperience, email, phone, password
                )
                _state.value = TeacherState.Registered
            } catch (e: Exception) {
                _state.value = TeacherState.Error(e.message ?: "Registration failed")
            }
        }
    }

    /** READ ONE (current profile) */
    fun fetchTeacher(id: String) {
        _state.value = TeacherState.Loading
        viewModelScope.launch {
            try {
                val resp = repo.getTeacher(id)       // TeacherResponse
                // map to our domain model Teacher
                val t = Teacher(
                    uid               = resp.id,
                    fullName          = resp.fullName,
                    subjectExpertise  = resp.subjectExpertise,
                    qualification     = resp.qualification,
                    yearsOfExperience = resp.yearsOfExperience,
                    email             = resp.email,
                    phone             = resp.phone
                )
                _state.value = TeacherState.DetailLoaded(t)
            } catch (e: Exception) {
                _state.value = TeacherState.Error(e.message ?: "Failed to load profile")
            }
        }
    }
}

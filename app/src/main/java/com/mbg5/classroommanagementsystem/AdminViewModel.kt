// File: app/src/main/java/com/mbg5/classroommanagementsystem/ui/signup/AdminViewModel.kt
package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegistrationState {
    object Idle    : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

class AdminViewModel(
    private val repo: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val state: StateFlow<RegistrationState> = _state

    fun registerAdmin(fullName: String, email: String, password: String, phone: String) {
        if (fullName.isBlank() || email.isBlank() || password.length < 8) {
            _state.value = RegistrationState.Error("Please fill all fields correctly.")
            return
        }
        viewModelScope.launch {
            _state.value = RegistrationState.Loading
            try {
                repo.registerAdmin(fullName, email, password, phone)
                _state.value = RegistrationState.Success
            } catch (e: Exception) {
                _state.value = RegistrationState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    fun reset() {
        _state.value = RegistrationState.Idle
    }
}

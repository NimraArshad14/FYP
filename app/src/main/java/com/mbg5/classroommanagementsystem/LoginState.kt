// File: app/src/main/java/com/mbg5/classroommanagementsystem/LoginState.kt
package com.mbg5.classroommanagementsystem

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val uid: String, val role: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

// File: app/src/main/java/com/mbg5/classroommanagementsystem/AuthViewModel.kt
package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val fire = Firebase.firestore

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    _loginState.value = LoginState.Error("No UID returned")
                    return@addOnSuccessListener
                }

                // 1) is admin?
                fire.collection("admins").document(uid).get()
                    .addOnSuccessListener { docA ->
                        if (docA.exists()) {
                            SessionManager.login(uid, "admin")
                            _loginState.value = LoginState.Success(uid, "admin")
                        } else {
                            // 2) teacher?
                            fire.collection("teachers").document(uid).get()
                                .addOnSuccessListener { docT ->
                                    if (docT.exists()) {
                                        SessionManager.login(uid, "teacher")
                                        _loginState.value = LoginState.Success(uid, "teacher")
                                    } else {
                                        // 3) student?
                                        fire.collection("students").document(uid).get()
                                            .addOnSuccessListener { docS ->
                                                if (docS.exists()) {
                                                    SessionManager.login(uid, "student")
                                                    _loginState.value = LoginState.Success(uid, "student")
                                                } else {
                                                    _loginState.value = LoginState.Error("No profile found")
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                _loginState.value = LoginState.Error(e.localizedMessage ?: "Error checking student")
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    _loginState.value = LoginState.Error(e.localizedMessage ?: "Error checking teacher")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        _loginState.value = LoginState.Error(e.localizedMessage ?: "Error checking admin")
                    }
            }
            .addOnFailureListener { err ->
                _loginState.value = LoginState.Error(err.localizedMessage ?: "Login failed")
            }
    }

    fun logout() {
        auth.signOut()
        SessionManager.logout()
        _loginState.value = LoginState.Idle
    }

    fun reset() {
        _loginState.value = LoginState.Idle
    }
}

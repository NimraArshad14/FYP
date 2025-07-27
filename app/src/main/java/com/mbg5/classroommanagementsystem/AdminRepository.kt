// File: app/src/main/java/com/mbg5/classroommanagementsystem/ui/signup/AdminRepository.kt
package com.mbg5.classroommanagementsystem

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val auth  = Firebase.auth
    private val fire  = Firebase.firestore

    /**
     * 1) create Auth user
     * 2) send verification email
     * 3) write /admins/{uid} document
     */
    suspend fun registerAdmin(
        fullName: String,
        email: String,
        password: String,
        phone: String
    ) {
        // --- 1) create user in Firebase Auth ---
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user   = result.user ?: throw Exception("Failed to create user")

        // --- 2) send them a verification email ---
        user.sendEmailVerification().await()

        // --- 3) write their profile doc under /admins/{uid} ---
        val profile = mapOf(
            "fullName" to fullName,
            "email"    to email,
            "phone"    to phone,
            "role"     to "admin"
        )
        fire.collection("admins")
            .document(user.uid)
            .set(profile)
            .await()
    }
}

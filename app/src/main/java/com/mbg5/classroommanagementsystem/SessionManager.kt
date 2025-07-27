// File: app/src/main/java/com/mbg5/classroommanagementsystem/SessionManager.kt
package com.mbg5.classroommanagementsystem

object SessionManager {
    /** The UID of the currently logged‐in user (or null if nobody). */
    var currentUserId: String? = null
        private set

    private var loggedIn: Boolean = false
    private var role: String? = null

    /**
     * Call this once you have successfully logged in or registered:
     *   userId = the Firebase UID
     *   asRole = "student" | "teacher" | "admin"
     */
    fun login(userId: String, asRole: String) {
        currentUserId = userId
        loggedIn = true
        role = asRole
    }

    fun logout() {
        currentUserId = null
        loggedIn = false
        role = null
    }

    fun isLoggedIn(): Boolean = loggedIn
    fun isStudent(): Boolean = role == "student"
    fun isTeacher(): Boolean = role == "teacher"
    fun isAdmin(): Boolean   = role == "admin"
}

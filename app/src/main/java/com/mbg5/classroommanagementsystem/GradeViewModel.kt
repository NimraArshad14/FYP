package com.mbg5.classroommanagementsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.GradeRequest
import com.mbg5.classroommanagementsystem.network.GradeResponse
import com.mbg5.classroommanagementsystem.network.StudentProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GradeUiState {
    object Idle : GradeUiState()
    object Loading : GradeUiState()
    data class ClassList(val grades: List<GradeResponse>) : GradeUiState()
    data class Error(val msg: String) : GradeUiState()
}

class GradeViewModel : ViewModel() {
    private val api = ApiClient.apiService

    private val _state = MutableStateFlow<GradeUiState>(GradeUiState.Idle)
    val state: StateFlow<GradeUiState> = _state

    var roster by mutableStateOf<List<StudentProfileResponse>>(emptyList())
        private set

    fun fetchClassData(classId: String) {
        viewModelScope.launch {
            _state.value = GradeUiState.Loading
            try {
                val userId = SessionManager.currentUserId
                    ?: throw IllegalStateException("Not logged in")

                val clsResp = api.getClass(userId, classId)
                if (clsResp.isSuccessful) {
                    roster = clsResp.body()!!.students
                }

                val gResp = api.listGradesForClass(userId, classId)
                if (gResp.isSuccessful) {
                    _state.value = GradeUiState.ClassList(gResp.body()!!)
                } else {
                    _state.value = GradeUiState.Error(
                        gResp.errorBody()?.string() ?: "Failed to load grades"
                    )
                }
            } catch (t: Throwable) {
                _state.value = GradeUiState.Error(t.localizedMessage ?: "Network error")
            }
        }
    }

    fun fetchMyGrades(classId: String, studentId: String) {
        viewModelScope.launch {
            _state.value = GradeUiState.Loading
            try {
                val gResp = api.listMyGradesForClass(studentId, classId)
                if (gResp.isSuccessful) {
                    _state.value = GradeUiState.ClassList(gResp.body()!!)
                } else {
                    _state.value = GradeUiState.Error(
                        gResp.errorBody()?.string() ?: "Failed to load grades"
                    )
                }
            } catch (t: Throwable) {
                _state.value = GradeUiState.Error(t.localizedMessage ?: "Network error")
            }
        }
    }

    fun createGrade(
        classId: String,
        studentId: String,
        value: String,
        comment: String?
    ) = saveGrade(classId, null, studentId, value, comment)

    fun updateGrade(
        classId: String,
        gradeId: String,
        studentId: String,
        value: String,
        comment: String?
    ) = saveGrade(classId, gradeId, studentId, value, comment)

    private fun saveGrade(
        classId: String,
        gradeId: String?,
        studentId: String,
        value: String,
        comment: String?
    ) {
        viewModelScope.launch {
            _state.value = GradeUiState.Loading
            try {
                val userId = SessionManager.currentUserId
                    ?: throw IllegalStateException("Not logged in")
                val req = GradeRequest(studentId, value, comment)

                val resp = if (gradeId == null) {
                    api.createGrade(userId, classId, req)
                } else {
                    api.updateGrade(userId, gradeId, req)
                }

                if (resp.isSuccessful) {
                    fetchClassData(classId)
                } else {
                    _state.value = GradeUiState.Error(
                        resp.errorBody()?.string() ?: "Save failed"
                    )
                }
            } catch (t: Throwable) {
                _state.value = GradeUiState.Error(t.localizedMessage ?: "Error")
            }
        }
    }
}
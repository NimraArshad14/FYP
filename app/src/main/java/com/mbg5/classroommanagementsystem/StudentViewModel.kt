// File: app/src/main/java/com/mbg5/classroommanagementsystem/StudentViewModel.kt
package com.mbg5.classroommanagementsystem

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.StudentRequest
import com.mbg5.classroommanagementsystem.network.StudentResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

sealed class StudentState {
    object Idle : StudentState()
    object Loading : StudentState()
    data class ListLoaded(val students: List<StudentResponse>) : StudentState()
    data class DetailLoaded(val student: StudentResponse) : StudentState()
    data class Created(val student: StudentResponse) : StudentState()
    data class Updated(val student: StudentResponse) : StudentState()
    data class Deleted(val id: String) : StudentState()
    data class Error(val message: String) : StudentState()
}

class StudentViewModel : ViewModel() {
    private val _state = MutableStateFlow<StudentState>(StudentState.Idle)
    val state: StateFlow<StudentState> = _state.asStateFlow()

    private val gson = Gson()

    fun reset() {
        _state.value = StudentState.Idle
    }

    /** CREATE */
    fun registerStudent(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        clazz: String,
        imageUri: Uri,
        contentResolver: ContentResolver
    ) {
        viewModelScope.launch {
            _state.value = StudentState.Loading
            try {
                // JSON part
                val reqObj = StudentRequest(fullName, email, password, phone, clazz)
                val json = gson.toJson(reqObj)
                val dataBody: RequestBody = json
                    .toRequestBody("application/json".toMediaType())

                // Image part
                val stream = contentResolver.openInputStream(imageUri)!!
                val bytes = stream.readBytes()
                val imageBody = bytes.toRequestBody("image/*".toMediaType())
                val imagePart = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "profile.jpg",
                    body = imageBody
                )

                val resp = ApiClient.apiService.createStudent(dataBody, imagePart)
                if (resp.isSuccessful) {
                    _state.value = StudentState.Created(resp.body()!!)
                } else {
                    val err = resp.errorBody()?.string() ?: "{}"
                    val jo = JSONObject(err)
                    val msg = jo.optString("message")
                        .ifBlank {
                            jo.keys().asSequence().firstOrNull()?.let { jo.getString(it) } ?: "Unknown error"
                        }
                    _state.value = StudentState.Error(msg)
                }
            } catch (t: Throwable) {
                _state.value = StudentState.Error(t.localizedMessage ?: "Error")
            }
        }
    }

    /** READ ALL */
    fun fetchStudents(id: String) {
        viewModelScope.launch {
            _state.value = StudentState.Loading
            try {
                val resp = ApiClient.apiService.listStudents()
                if (resp.isSuccessful) {
                    _state.value = StudentState.ListLoaded(resp.body()!!)
                } else {
                    _state.value = StudentState.Error("Failed to load students")
                }
            } catch (t: Throwable) {
                _state.value = StudentState.Error(t.localizedMessage ?: "Error")
            }
        }
    }

    /** READ ONE */
    fun fetchStudent(id: String) {
        viewModelScope.launch {
            _state.value = StudentState.Loading
            try {
                val resp = ApiClient.apiService.getStudent(id)
                if (resp.isSuccessful) {
                    _state.value = StudentState.DetailLoaded(resp.body()!!)
                } else {
                    _state.value = StudentState.Error("Failed to load student")
                }
            } catch (t: Throwable) {
                _state.value = StudentState.Error(t.localizedMessage ?: "Error")
            }
        }
    }

    /** UPDATE (no image) */
    fun updateStudent(
        id: String,
        fullName: String,
        email: String,
        phone: String,
        clazz: String
    ) {
        viewModelScope.launch {
            _state.value = StudentState.Loading
            try {
                val req = StudentRequest(fullName, email, "", phone, clazz)
                val resp = ApiClient.apiService.updateStudent(id, req)
                if (resp.isSuccessful) {
                    _state.value = StudentState.Updated(resp.body()!!)
                } else {
                    _state.value = StudentState.Error("Failed to update")
                }
            } catch (t: Throwable) {
                _state.value = StudentState.Error(t.localizedMessage ?: "Error")
            }
        }
    }

    /** DELETE */
    fun deleteStudent(id: String) {
        viewModelScope.launch {
            _state.value = StudentState.Loading
            try {
                val resp = ApiClient.apiService.deleteStudent(id)
                if (resp.isSuccessful) {
                    _state.value = StudentState.Deleted(id)
                } else {
                    _state.value = StudentState.Error("Failed to delete")
                }
            } catch (t: Throwable) {
                _state.value = StudentState.Error(t.localizedMessage ?: "Error")
            }
        }
    }
}

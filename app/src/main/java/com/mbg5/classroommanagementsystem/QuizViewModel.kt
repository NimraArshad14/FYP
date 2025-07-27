// QuizViewModel.kt
package com.mbg5.classroommanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.Question
import com.mbg5.classroommanagementsystem.network.Quiz
import com.mbg5.classroommanagementsystem.network.QuizAttempt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    private val api = ApiClient.apiService

    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz: StateFlow<Quiz?> = _quiz

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes

    private val _attempts = MutableStateFlow<List<QuizAttempt>>(emptyList())
    val attempts: StateFlow<List<QuizAttempt>> = _attempts

    fun fetchQuizzesForClass(classId: String) = viewModelScope.launch {
        val response = api.getQuizzesByClass(classId)
        if (response.isSuccessful) {
            _quizzes.value = response.body() ?: emptyList()
        }
    }

    fun fetchQuiz(quizId: String) = viewModelScope.launch {
        val quiz = _quizzes.value.find { it.id == quizId }
        _quiz.value = quiz
    }

    fun createQuiz(classId: String, title: String, questions: List<Question>) = viewModelScope.launch {
        val response = api.createQuiz(classId, title, questions)
        if (response.isSuccessful) {
            fetchQuizzesForClass(classId)
        }
    }

    fun submitAttempt(quizId: String, answers: Map<String, String>) = viewModelScope.launch {
        val response = api.submitQuizAttempt(quizId, answers)
        if (response.isSuccessful) {
            // Handle success
        }
    }

    fun fetchStudentAttempts(quizId: String) = viewModelScope.launch {
        val response = api.getStudentAttempts(quizId)
        if (response.isSuccessful) {
            _attempts.value = response.body() ?: emptyList()
        }
    }
}
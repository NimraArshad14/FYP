// File: app/src/main/java/com/mbg5/classroommanagementsystem/StudentQuizAttemptScreen.kt
package com.mbg5.classroommanagementsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQuizAttemptScreen(
    quizId: String,
    navController: NavHostController,
    vm: QuizViewModel = viewModel()
) {
    val quiz by vm.quiz.collectAsState()
    val answers = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(quizId) {
        vm.fetchQuiz(quizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(quiz?.title ?: "Quiz") })
        },
        bottomBar = {
            SimpleBottomNavigation(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            quiz?.questions?.let { questions ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(questions) { question ->
                        QuestionCard(
                            question = question,
                            onAnswerSelected = { selectedAnswer ->
                                answers[question.id] = selectedAnswer
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        vm.submitAttempt(quizId, answers)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Submit Quiz")
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading quiz...")
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    onAnswerSelected: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(question.text, style = MaterialTheme.typography.bodyLarge)

        question.options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedOption = option
                        onAnswerSelected(option)
                    }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (selectedOption == option),
                    onClick = {
                        selectedOption = option
                        onAnswerSelected(option)
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(option)
            }
        }
    }
}

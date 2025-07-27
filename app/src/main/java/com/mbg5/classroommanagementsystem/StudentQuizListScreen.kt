// File: app/src/main/java/com/mbg5/classroommanagementsystem/StudentQuizListScreen.kt
package com.mbg5.classroommanagementsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQuizListScreen(
    classId: String,
    navController: NavHostController,
    vm: QuizViewModel = viewModel()
) {
    // Collect the list of quizzes for this class
    val quizzes by vm.quizzes.collectAsState()

    // Fetch when the screen is first composed or classId changes
    LaunchedEffect(classId) {
        vm.fetchQuizzesForClass(classId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quizzes") })
        },
        bottomBar = {
            SimpleBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(quizzes) { quiz ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = quiz.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = {
                                navController.navigate("attemptQuiz/${quiz.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Attempt Quiz")
                        }
                    }
                }
            }
        }
    }
}

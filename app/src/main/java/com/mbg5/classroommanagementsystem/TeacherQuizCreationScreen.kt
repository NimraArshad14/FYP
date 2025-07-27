// File: app/src/main/java/com/mbg5/classroommanagementsystem/TeacherQuizCreationScreen.kt
package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.Question
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQuizCreationScreen(
    classId: String,
    navController: NavHostController,
    vm: QuizViewModel = viewModel()
) {
    // ORIGINAL STATE VARIABLES - UNCHANGED
    var title by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(listOf<Question>()) }
    val scrollState = rememberScrollState()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // Enhanced UI Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6a11cb),
                        Color(0xFF2575fc),
                        Color(0xFF6a11cb)
                    )
                )
            )
    ) {
        // Animated background particles
        QuizCreationBackgroundParticles()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                EnhancedTopAppBar(
                    title = "Create Quiz",
                    onBackClick = { navController.navigateUp() }
                )
            },
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { paddingValues ->
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Enhanced Quiz Header
                    QuizCreationHeader()

                    // Enhanced Quiz title input with ORIGINAL FUNCTIONALITY
                    EnhancedTitleInput(
                        title = title,
                        onTitleChange = { title = it } // ORIGINAL LOGIC - UNCHANGED
                    )

                    // Enhanced Add Question Button with ORIGINAL FUNCTIONALITY
                    EnhancedAddQuestionButton(
                        onClick = {
                            // ORIGINAL ADD QUESTION LOGIC - UNCHANGED
                            val newQuestion = Question(
                                id = (questions.size + 1).toString(),
                                text = "",
                                options = listOf("", "", "", ""),
                                correctAnswer = ""
                            )
                            questions = questions + newQuestion
                        }
                    )

                    // Enhanced Questions List with ORIGINAL FUNCTIONALITY
                    questions.forEachIndexed { index, question ->
                        AnimatedVisibility(
                            visible = showContent,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = index * 150,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 600,
                                    delayMillis = index * 150
                                )
                            )
                        ) {
                            // Enhanced Question Form with ORIGINAL FUNCTIONALITY
                            EnhancedQuestionForm(
                                question = question,
                                questionNumber = index + 1,
                                onQuestionChange = { updated ->
                                    // ORIGINAL QUESTION UPDATE LOGIC - UNCHANGED
                                    questions = questions.map {
                                        if (it.id == updated.id) updated else it
                                    }
                                },
                                onDeleteQuestion = {
                                    // Enhanced delete functionality
                                    questions = questions.filter { it.id != question.id }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Enhanced Create Quiz Button with ORIGINAL FUNCTIONALITY
                    EnhancedCreateQuizButton(
                        enabled = title.isNotBlank() && questions.isNotEmpty(),
                        onClick = {
                            // ORIGINAL CREATE QUIZ LOGIC - UNCHANGED
                            vm.createQuiz(classId, title, questions)
                            navController.popBackStack()
                        }
                    )

                    // Bottom padding for better scrolling
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF9800),
                                    Color(0xFFE65100)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Quiz,
                        contentDescription = "Quiz",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun QuizCreationHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.White.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Quiz Icon
            val infiniteTransition = rememberInfiniteTransition(label = "quiz_glow")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow"
            )

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6a11cb).copy(alpha = glowAlpha),
                                Color(0xFF2575fc).copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Quiz,
                    contentDescription = "Quiz",
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Create New Quiz",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                )
                Text(
                    "Design engaging questions for your students",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
            }
        }
    }
}

@Composable
private fun EnhancedTitleInput(
    title: String,
    onTitleChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF6a11cb).copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Title,
                    contentDescription = "Title",
                    tint = Color(0xFF6a11cb),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Quiz Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ORIGINAL TITLE INPUT - UNCHANGED
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Quiz Title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Title",
                        tint = Color(0xFF6a11cb)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6a11cb),
                    focusedLabelColor = Color(0xFF6a11cb),
                    focusedLeadingIconColor = Color(0xFF6a11cb)
                )
            )
        }
    }
}

@Composable
private fun EnhancedAddQuestionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "Add Question",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
fun EnhancedQuestionForm(
    question: Question,
    questionNumber: Int,
    onQuestionChange: (Question) -> Unit,
    onDeleteQuestion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0xFF2575fc).copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF2575fc),
                                        Color(0xFF6a11cb)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            questionNumber.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Question $questionNumber",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                    )
                }

                IconButton(
                    onClick = onDeleteQuestion,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Question",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ORIGINAL QUESTION TEXT INPUT - UNCHANGED
            OutlinedTextField(
                value = question.text,
                onValueChange = { onQuestionChange(question.copy(text = it)) },
                label = { Text("Question Text") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Question",
                        tint = Color(0xFF2575fc)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2575fc),
                    focusedLabelColor = Color(0xFF2575fc),
                    focusedLeadingIconColor = Color(0xFF2575fc)
                ),
                minLines = 2
            )

            // Options Section
            Text(
                "Answer Options",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
            )

            // ORIGINAL OPTIONS INPUTS - UNCHANGED
            question.options.forEachIndexed { index, option ->
                val optionColors = listOf(
                    Color(0xFF4CAF50),
                    Color(0xFF2196F3),
                    Color(0xFFFF9800),
                    Color(0xFF9C27B0)
                )

                OutlinedTextField(
                    value = option,
                    onValueChange = { newOpt ->
                        // ORIGINAL OPTION UPDATE LOGIC - UNCHANGED
                        val updatedOptions = question.options.toMutableList().apply {
                            this[index] = newOpt
                        }
                        onQuestionChange(question.copy(options = updatedOptions))
                    },
                    label = { Text("Option ${index + 1}") },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(optionColors[index].copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                ('A' + index).toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = optionColors[index]
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = optionColors[index],
                        focusedLabelColor = optionColors[index]
                    )
                )
            }

            // ORIGINAL CORRECT ANSWER INPUT - UNCHANGED
            OutlinedTextField(
                value = question.correctAnswer,
                onValueChange = { onQuestionChange(question.copy(correctAnswer = it)) },
                label = { Text("Correct Answer") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Correct Answer",
                        tint = Color(0xFF4CAF50)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    focusedLabelColor = Color(0xFF4CAF50),
                    focusedLeadingIconColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}

@Composable
private fun EnhancedCreateQuizButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = if (enabled) 16.dp else 4.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = if (enabled) Color(0xFF6a11cb).copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.1f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF6a11cb) else Color.Gray,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(30.dp)
    ) {
        if (enabled) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Create",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            if (enabled) "Create Quiz" else "Please add title and questions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun QuizCreationBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(25) {
            QuizParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                color = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.05f),
                    Color(0xFF6a11cb).copy(alpha = 0.1f),
                    Color(0xFF2575fc).copy(alpha = 0.1f),
                    Color(0xFFFF9800).copy(alpha = 0.1f)
                ).random(),
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "quiz_particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.x + time * particle.speed) % 1f * size.width
            val y = (particle.y + sin(time * 2f + particle.phase) * 0.1f) * size.height

            drawCircle(
                color = particle.color,
                radius = particle.size * density.density,
                center = Offset(x, y)
            )
        }
    }
}

private data class QuizParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val phase: Float
)
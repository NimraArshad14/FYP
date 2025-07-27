package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random

@Composable
fun SmartQuizScreen(navController: NavHostController) {
    var currentScreen by remember { mutableStateOf(SmartQuizScreenState.WELCOME) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb),
                        Color(0xFFf5576c)
                    )
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles()

        when (currentScreen) {
            SmartQuizScreenState.WELCOME -> {
                WelcomeScreen(
                    isVisible = isVisible,
                    onStartQuiz = { subject ->
                        selectedSubject = subject
                        currentScreen = SmartQuizScreenState.LOADING
                        isLoading = true
                    }
                )
            }
            SmartQuizScreenState.LOADING -> {
                LoadingScreen(
                    subject = selectedSubject!!,
                    onLoadingComplete = {
                        isLoading = false
                        currentScreen = SmartQuizScreenState.QUIZ
                        totalQuestions = selectedSubject!!.questions.size
                    }
                )
            }
            SmartQuizScreenState.QUIZ -> {
                QuizScreen(
                    subject = selectedSubject!!,
                    currentQuestionIndex = currentQuestionIndex,
                    score = score,
                    totalQuestions = totalQuestions,
                    onAnswerSelected = { isCorrect ->
                        if (isCorrect) score++
                        if (currentQuestionIndex < selectedSubject!!.questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            currentScreen = SmartQuizScreenState.RESULTS
                        }
                    }
                )
            }
            SmartQuizScreenState.RESULTS -> {
                ResultsScreen(
                    score = score,
                    totalQuestions = totalQuestions,
                    subject = selectedSubject!!,
                    onRetry = {
                        currentQuestionIndex = 0
                        score = 0
                        currentScreen = SmartQuizScreenState.QUIZ
                    },
                    onBackToHome = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Bottom navigation
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SimpleBottomNavigation(navController = navController)
        }
    }
}

@Composable
private fun WelcomeScreen(
    isVisible: Boolean,
    onStartQuiz: (Subject) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(1000))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(60.dp))
                
                // AI Brain Icon with Animation
                AnimatedBrainIcon()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title
                Text(
                    text = "AI Smart Quiz",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Experience the future of learning",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            item {
                Text(
                    text = "Choose your subject:",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onStartQuiz(subject) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun AnimatedBrainIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "brain")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00D4FF),
                        Color(0xFF5B73FF),
                        Color(0xFF9C27B0)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Psychology,
            contentDescription = "AI Brain",
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer { rotationZ = rotation },
            tint = Color.White
        )
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = subject.gradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = subject.name,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "${subject.questions.size} questions",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Start",
                tint = subject.gradientColors[0],
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    subject: Subject,
    onLoadingComplete: () -> Unit
) {
    var loadingProgress by remember { mutableStateOf(0f) }
    var loadingText by remember { mutableStateOf("Initializing AI...") }
    
    LaunchedEffect(Unit) {
        val loadingSteps = listOf(
            "Initializing AI..." to 0.2f,
            "Analyzing ${subject.name}..." to 0.4f,
            "Generating questions..." to 0.6f,
            "Optimizing difficulty..." to 0.8f,
            "Ready to start!" to 1.0f
        )
        
        loadingSteps.forEach { (text, progress) ->
            loadingText = text
            delay(800)
            loadingProgress = progress
        }
        delay(500)
        onLoadingComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated AI Brain
        AnimatedBrainIcon()
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Progress Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(loadingProgress)
                    .height(8.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = subject.gradientColors
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = loadingText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuizScreen(
    subject: Subject,
    currentQuestionIndex: Int,
    score: Int,
    totalQuestions: Int,
    onAnswerSelected: (Boolean) -> Unit
) {
    val currentQuestion = subject.questions[currentQuestionIndex]
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentQuestionIndex) {
        selectedAnswer = null
        showResult = false
    }

    // Auto-advance after showing result
    LaunchedEffect(showResult) {
        if (showResult) {
            delay(2000)
            selectedAnswer?.let { answer ->
                onAnswerSelected(answer == currentQuestion.correctAnswer)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Progress Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                    )
                    Text(
                        text = "$score/$totalQuestions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = subject.gradientColors[0]
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat())
                            .height(6.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = subject.gradientColors
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }

        // Question
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = currentQuestion.text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Answer Options
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentQuestion.options) { option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswer == option,
                    isCorrect = if (showResult) option == currentQuestion.correctAnswer else null,
                    onClick = {
                        if (selectedAnswer == null) {
                            selectedAnswer = option
                            showResult = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> Color(0xFF4CAF50)
        isCorrect == false -> Color(0xFFF44336)
        isSelected -> Color(0xFF2196F3)
        else -> Color.White.copy(alpha = 0.95f)
    }
    
    val textColor = when {
        isCorrect != null -> Color.White
        isSelected -> Color.White
        else -> Color(0xFF1A1A2E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCorrect != null) {
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun ResultsScreen(
    score: Int,
    totalQuestions: Int,
    subject: Subject,
    onRetry: () -> Unit,
    onBackToHome: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions.toFloat()) * 100
    val grade = when {
        percentage >= 90 -> "A+"
        percentage >= 80 -> "A"
        percentage >= 70 -> "B"
        percentage >= 60 -> "C"
        percentage >= 50 -> "D"
        else -> "F"
    }
    
    val message = when {
        percentage >= 90 -> "Excellent! You're a master!"
        percentage >= 80 -> "Great job! You really know your stuff!"
        percentage >= 70 -> "Good work! Keep it up!"
        percentage >= 60 -> "Not bad! Room for improvement."
        percentage >= 50 -> "You passed! Study a bit more."
        else -> "Don't worry! Practice makes perfect!"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Result Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (percentage >= 70) {
                                listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                            } else {
                                listOf(Color(0xFFFF9800), Color(0xFFE65100))
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (percentage >= 70) Icons.Filled.EmojiEvents else Icons.Filled.School,
                    contentDescription = "Result",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        item {
            Text(
                text = "Quiz Complete!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$score/$totalQuestions",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = subject.gradientColors[0],
                            fontSize = 36.sp
                        )
                    )
                    
                    Text(
                        text = "${percentage.toInt()}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            fontSize = 24.sp
                        )
                    )
                    
                    Text(
                        text = "Grade: $grade",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                            fontSize = 18.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF1A1A2E),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = subject.gradientColors[0]
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again")
                }
                
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF666666)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Home")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun AnimatedBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(50) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1,
                speed = Random.nextFloat() * 1.5f + 0.5f,
                color = Color.White
            )
        }
    }
    val time by rememberInfiniteTransition(label = "time").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        for (p in particles) {
            val currentX = (p.x * width + p.speed * time * 30) % width
            val currentY = (p.y * height + p.speed * time * 30) % height

            drawCircle(
                color = p.color.copy(alpha = (sin(time * 2 * PI.toFloat()) * 0.3f + 0.7f)),
                radius = p.size * with(density) { 1.dp.toPx() },
                center = Offset(currentX, currentY)
            )
        }
    }
}

// Data Classes
enum class SmartQuizScreenState {
    WELCOME, LOADING, QUIZ, RESULTS
}

data class Subject(
    val name: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)

// Sample Data
val subjects = listOf(
    Subject(
        name = "Mathematics",
        icon = Icons.Filled.Calculate,
        gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
        questions = listOf(
            QuizQuestion(
                text = "What is the value of π (pi) to two decimal places?",
                options = listOf("3.12", "3.14", "3.16", "3.18"),
                correctAnswer = "3.14"
            ),
            QuizQuestion(
                text = "What is the square root of 144?",
                options = listOf("10", "11", "12", "13"),
                correctAnswer = "12"
            ),
            QuizQuestion(
                text = "What is 15% of 200?",
                options = listOf("25", "30", "35", "40"),
                correctAnswer = "30"
            ),
            QuizQuestion(
                text = "What is the next number in the sequence: 2, 4, 8, 16, ...?",
                options = listOf("24", "32", "30", "28"),
                correctAnswer = "32"
            ),
            QuizQuestion(
                text = "What is the area of a circle with radius 5?",
                options = listOf("25π", "50π", "75π", "100π"),
                correctAnswer = "25π"
            )
        )
    ),
    Subject(
        name = "Science",
        icon = Icons.Filled.Science,
        gradientColors = listOf(Color(0xFF2196F3), Color(0xFF1976D2)),
        questions = listOf(
            QuizQuestion(
                text = "What is the chemical symbol for gold?",
                options = listOf("Ag", "Au", "Fe", "Cu"),
                correctAnswer = "Au"
            ),
            QuizQuestion(
                text = "What is the largest planet in our solar system?",
                options = listOf("Mars", "Venus", "Jupiter", "Saturn"),
                correctAnswer = "Jupiter"
            ),
            QuizQuestion(
                text = "What is the hardest natural substance on Earth?",
                options = listOf("Steel", "Diamond", "Granite", "Iron"),
                correctAnswer = "Diamond"
            ),
            QuizQuestion(
                text = "What is the main component of the sun?",
                options = listOf("Liquid lava", "Molten iron", "Hot plasma", "Solid rock"),
                correctAnswer = "Hot plasma"
            ),
            QuizQuestion(
                text = "What is the atomic number of carbon?",
                options = listOf("4", "6", "8", "12"),
                correctAnswer = "6"
            )
        )
    ),
    Subject(
        name = "History",
        icon = Icons.Filled.History,
        gradientColors = listOf(Color(0xFFFF9800), Color(0xFFE65100)),
        questions = listOf(
            QuizQuestion(
                text = "In which year did World War II end?",
                options = listOf("1943", "1944", "1945", "1946"),
                correctAnswer = "1945"
            ),
            QuizQuestion(
                text = "Who was the first President of the United States?",
                options = listOf("John Adams", "Thomas Jefferson", "George Washington", "Benjamin Franklin"),
                correctAnswer = "George Washington"
            ),
            QuizQuestion(
                text = "What ancient wonder was located in Alexandria?",
                options = listOf("Colossus of Rhodes", "Lighthouse", "Hanging Gardens", "Temple of Artemis"),
                correctAnswer = "Lighthouse"
            ),
            QuizQuestion(
                text = "Which empire was ruled by the Aztecs?",
                options = listOf("Mexican Empire", "Incan Empire", "Mayan Empire", "Aztec Empire"),
                correctAnswer = "Aztec Empire"
            ),
            QuizQuestion(
                text = "What year did Columbus discover America?",
                options = listOf("1490", "1491", "1492", "1493"),
                correctAnswer = "1492"
            )
        )
    ),
    Subject(
        name = "Literature",
        icon = Icons.Filled.Book,
        gradientColors = listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A)),
        questions = listOf(
            QuizQuestion(
                text = "Who wrote 'Romeo and Juliet'?",
                options = listOf("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
                correctAnswer = "William Shakespeare"
            ),
            QuizQuestion(
                text = "What is the main character's name in 'The Great Gatsby'?",
                options = listOf("Jay Gatsby", "Nick Carraway", "Daisy Buchanan", "Tom Buchanan"),
                correctAnswer = "Jay Gatsby"
            ),
            QuizQuestion(
                text = "What type of poem has 14 lines?",
                options = listOf("Haiku", "Sonnet", "Limerick", "Ballad"),
                correctAnswer = "Sonnet"
            ),
            QuizQuestion(
                text = "Who wrote 'Pride and Prejudice'?",
                options = listOf("Emily Brontë", "Charlotte Brontë", "Jane Austen", "Mary Shelley"),
                correctAnswer = "Jane Austen"
            ),
            QuizQuestion(
                text = "What is the setting of 'Lord of the Flies'?",
                options = listOf("A desert island", "A city", "A forest", "A mountain"),
                correctAnswer = "A desert island"
            )
        )
    )
)

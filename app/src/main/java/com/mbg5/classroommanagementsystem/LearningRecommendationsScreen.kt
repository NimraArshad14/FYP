package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class AIRecommendation(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val priority: String,
    val estimatedTime: String,
    val difficulty: String,
    val category: String
)

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class FAQItem(
    val question: String,
    val answer: String,
    val keywords: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningRecommendationsScreen(
    navController: NavHostController
) {
    var isAIThinking by remember { mutableStateOf(false) }
    var currentRecommendations by remember { mutableStateOf(getRandomRecommendations()) }
    var showRecommendations by remember { mutableStateOf(false) }
    var aiMessageIndex by remember { mutableStateOf(0) }
    var shouldGenerateRecommendations by remember { mutableStateOf(false) }
    var showChatbot by remember { mutableStateOf(false) }

    val aiMessages = listOf(
        "Analyzing your learning patterns...",
        "Processing your academic performance...",
        "Consulting educational databases...",
        "Generating personalized recommendations...",
        "Optimizing study strategies for you...",
        "Finalizing your learning roadmap..."
    )

    LaunchedEffect(Unit) {
        delay(500)
        showRecommendations = true
    }

    LaunchedEffect(shouldGenerateRecommendations) {
        if (shouldGenerateRecommendations) {
            isAIThinking = true
            aiMessageIndex = 0

            repeat(aiMessages.size) { index ->
                aiMessageIndex = index
                delay(800)
            }
            currentRecommendations = getRandomRecommendations()
            delay(500)
            isAIThinking = false
            shouldGenerateRecommendations = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF24243e),
                        Color(0xFF302B63),
                        Color(0xFF0F0C29)
                    )
                )
            )
    ) {
        AIParticleBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
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
                                                Color(0xFF00D4FF),
                                                Color(0xFF5B73FF)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Psychology,
                                    contentDescription = "AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "AI Learning Assistant",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showChatbot = !showChatbot }
                        ) {
                            Icon(
                                imageVector = if (showChatbot) Icons.Filled.Close else Icons.Filled.Chat,
                                contentDescription = if (showChatbot) "Close Chat" else "Open Chat",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { paddingValues ->
            if (showChatbot) {
                FAQChatbot(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        AnimatedVisibility(
                            visible = showRecommendations,
                            enter = slideInVertically(
                                initialOffsetY = { -it },
                                animationSpec = tween(800, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(800))
                        ) {
                            AIAssistantHeader(
                                isThinking = isAIThinking,
                                currentMessage = if (isAIThinking) aiMessages[aiMessageIndex] else "Ready to help you excel!"
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = showRecommendations,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(1000, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(1000))
                        ) {
                            Button(
                                onClick = {
                                    if (!isAIThinking) {
                                        shouldGenerateRecommendations = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .shadow(
                                        elevation = 16.dp,
                                        shape = RoundedCornerShape(30.dp),
                                        ambientColor = Color(0xFF00D4FF).copy(alpha = 0.3f)
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAIThinking) Color(0xFF666666) else Color(0xFF00D4FF)
                                ),
                                shape = RoundedCornerShape(30.dp),
                                enabled = !isAIThinking
                            ) {
                                if (isAIThinking) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "AI is thinking...",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "Generate",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Generate New Recommendations",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (!isAIThinking) {
                        itemsIndexed(currentRecommendations) { index, recommendation ->
                            AnimatedVisibility(
                                visible = showRecommendations,
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
                                RecommendationCard(
                                    recommendation = recommendation,
                                    index = index
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FAQChatbot(
    modifier: Modifier = Modifier
) {
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var currentMessage by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var messageCounter by remember { mutableStateOf(0) }

    val faqDatabase = remember {
        listOf(
            FAQItem(
                question = "How can I improve my study habits?",
                answer = "Here are some effective study techniques:\n• Use the Pomodoro Technique (25-min focused sessions)\n• Create a dedicated study space\n• Take regular breaks\n• Practice active recall\n• Use spaced repetition for memorization",
                keywords = listOf("study", "habits", "improve", "techniques", "better")
            ),
            FAQItem(
                question = "What's the best way to prepare for exams?",
                answer = "Effective exam preparation includes:\n• Start studying early (don't cram)\n• Create a study schedule\n• Practice with past papers\n• Form study groups\n• Get enough sleep before the exam\n• Review key concepts regularly",
                keywords = listOf("exam", "test", "prepare", "preparation", "ready")
            ),
            FAQItem(
                question = "How do I manage my time better?",
                answer = "Time management strategies:\n• Use a planner or digital calendar\n• Prioritize tasks using the Eisenhower Matrix\n• Set specific goals and deadlines\n• Eliminate distractions\n• Learn to say no to non-essential activities\n• Block time for important tasks",
                keywords = listOf("time", "manage", "management", "schedule", "organize", "planning")
            ),
            FAQItem(
                question = "I'm feeling overwhelmed with schoolwork. What should I do?",
                answer = "When feeling overwhelmed:\n• Break large tasks into smaller ones\n• Talk to teachers or counselors\n• Take breaks and practice self-care\n• Ask for help from friends or family\n• Consider reducing extracurricular activities temporarily\n• Focus on one task at a time",
                keywords = listOf("overwhelmed", "stress", "stressed", "help", "anxiety", "too much")
            ),
            FAQItem(
                question = "How can I stay motivated to study?",
                answer = "Staying motivated tips:\n• Set clear, achievable goals\n• Reward yourself for completing tasks\n• Find your 'why' - remember your long-term goals\n• Study with friends for accountability\n• Change your study environment occasionally\n• Celebrate small wins",
                keywords = listOf("motivation", "motivated", "lazy", "procrastination", "focus")
            ),
            FAQItem(
                question = "What are some effective note-taking methods?",
                answer = "Popular note-taking methods:\n• Cornell Note-Taking System\n• Mind mapping for visual learners\n• Outline method for structured content\n• Charting method for comparing information\n• Digital tools like Notion or OneNote\n• Use colors and symbols for organization",
                keywords = listOf("notes", "note-taking", "writing", "organize", "method")
            )
        )
    }

    fun findBestAnswer(query: String): String {
        val queryLower = query.lowercase()
        val matchingFAQ = faqDatabase.find { faq ->
            faq.keywords.any { keyword -> queryLower.contains(keyword) }
        }
        return matchingFAQ?.answer ?: "I'm sorry, I don't have specific information about that. Here are some topics I can help with:\n• Study habits and techniques\n• Exam preparation\n• Time management\n• Dealing with stress\n• Note-taking methods\n• Staying motivated\n\nTry asking about any of these topics!"
    }

    LaunchedEffect(Unit) {
        chatMessages = listOf(
            ChatMessage(
                message = "Hi! I'm your AI Study Assistant. I can help you with study tips, exam preparation, time management, and more. What would you like to know?",
                isUser = false
            )
        )
    }

    LaunchedEffect(messageCounter) {
        if (messageCounter > 0) {
            val lastUserMessage = chatMessages.last { it.isUser }.message
            val response = findBestAnswer(lastUserMessage)
            delay(1500)
            val aiMessage = ChatMessage(response, false)
            chatMessages = chatMessages + aiMessage
            isTyping = false
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00D4FF),
                                    Color(0xFF5B73FF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Study Assistant",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                    )
                    Text(
                        "FAQ-based AI Helper",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF666666)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = true
        ) {
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }

            items(chatMessages.reversed()) { message ->
                ChatBubble(message = message)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask me about studying...") },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (currentMessage.isNotBlank() && !isTyping) {
                                val userMessage = ChatMessage(currentMessage, true)
                                chatMessages = chatMessages + userMessage
                                currentMessage = ""
                                isTyping = true
                                messageCounter++
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (currentMessage.isNotBlank() && !isTyping) {
                            val userMessage = ChatMessage(currentMessage, true)
                            chatMessages = chatMessages + userMessage
                            currentMessage = ""
                            isTyping = true
                            messageCounter++
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentMessage.isNotBlank() && !isTyping)
                                Color(0xFF00D4FF)
                            else
                                Color(0xFF666666)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00D4FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser)
                    Color(0xFF00D4FF)
                else
                    Color.White.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (message.isUser) Color.White else Color(0xFF1A1A2E),
                    lineHeight = 20.sp
                )
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF666666)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF00D4FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SmartToy,
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF666666).copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
private fun AIAssistantHeader(
    isThinking: Boolean,
    currentMessage: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF00D4FF).copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (isThinking) 1.1f else 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isThinking) 1000 else 2000,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            // Use Animatable for rotation
            val rotation = remember { Animatable(0f) }

            LaunchedEffect(isThinking) {
                if (isThinking) {
                    rotation.animateTo(
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                } else {
                    rotation.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300)  // Smooth reset to 0
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .rotate(rotation.value)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF00D4FF),
                                Color(0xFF5B73FF),
                                Color(0xFF9C27B0),
                                Color(0xFF00D4FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = "AI Assistant",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Your Personal AI Study Coach",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = currentMessage,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                },
                label = "message"
            ) { message ->
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: AIRecommendation,
    index: Int
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = recommendation.color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
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
                val infiniteTransition = rememberInfiniteTransition(label = "icon_glow")
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.7f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow"
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    recommendation.color.copy(alpha = glowAlpha),
                                    recommendation.color.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = recommendation.icon,
                        contentDescription = recommendation.title,
                        modifier = Modifier.size(30.dp),
                        tint = recommendation.color
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        recommendation.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityChip(recommendation.priority)
                        TimeChip(recommendation.estimatedTime)
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                recommendation.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF444444),
                    lineHeight = 22.sp
                )
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailChip(
                            label = "Category",
                            value = recommendation.category,
                            icon = Icons.Outlined.Category,
                            color = Color(0xFF2196F3)
                        )
                        DetailChip(
                            label = "Difficulty",
                            value = recommendation.difficulty,
                            icon = Icons.Outlined.TrendingUp,
                            color = Color(0xFFFF9800)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Start studying action */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = recommendation.color
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Start This Study Session",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: String) {
    val color = when (priority.lowercase()) {
        "high" -> Color(0xFFFF5252)
        "medium" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Text(
            priority,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun TimeChip(time: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6C63FF).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = "Time",
                modifier = Modifier.size(12.dp),
                tint = Color(0xFF6C63FF)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                time,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6C63FF),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun DetailChip(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF666666),
                fontSize = 10.sp
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1A1A2E),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun AIParticleBackground() {
    val density = LocalDensity.current
    val particles = remember {
        List(40) {
            AIParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.1f + 0.02f,
                color = listOf(
                    Color(0x1000D4FF),
                    Color(0x105B73FF),
                    Color(0x109C27B0),
                    Color(0x1000BCD4)
                ).random(),
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ai_particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.x + time * particle.speed) % 1f * size.width
            val y = (particle.y + sin(time * 2f + particle.phase) * 0.1f) * size.height

            val pulseSize = particle.size * (1f + sin(time * 4f + particle.phase) * 0.3f)

            drawCircle(
                color = particle.color,
                radius = pulseSize * density.density,
                center = Offset(x, y)
            )
        }
    }
}

private data class AIParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val phase: Float
)

private fun getRandomRecommendations(): List<AIRecommendation> {
    val allRecommendations = listOf(
        AIRecommendation(
            title = "Focus on Data Structures",
            description = "Based on your recent quiz performance, I recommend spending extra time on binary trees and graph algorithms. Practice with visual diagrams to improve understanding.",
            icon = Icons.Filled.AccountTree,
            color = Color(0xFF4CAF50),
            priority = "High",
            estimatedTime = "45 min",
            difficulty = "Advanced",
            category = "Computer Science"
        ),
        AIRecommendation(
            title = "Review Mathematical Concepts",
            description = "Your calculus fundamentals need reinforcement. I suggest reviewing derivatives and integrals with practical examples to strengthen your foundation.",
            icon = Icons.Filled.Calculate,
            color = Color(0xFF2196F3),
            priority = "Medium",
            estimatedTime = "30 min",
            difficulty = "Intermediate",
            category = "Mathematics"
        ),
        AIRecommendation(
            title = "Practice Writing Skills",
            description = "Enhance your essay writing with structured practice. Focus on thesis development and supporting arguments for better academic performance.",
            icon = Icons.Filled.Edit,
            color = Color(0xFFFF9800),
            priority = "Medium",
            estimatedTime = "25 min",
            difficulty = "Beginner",
            category = "Language Arts"
        ),
        AIRecommendation(
            title = "Study Group Session",
            description = "Join a collaborative study session with your classmates. Peer learning can help clarify complex concepts and improve retention.",
            icon = Icons.Filled.Group,
            color = Color(0xFF9C27B0),
            priority = "Low",
            estimatedTime = "60 min",
            difficulty = "Intermediate",
            category = "Collaboration"
        ),
        AIRecommendation(
            title = "Memory Palace Technique",
            description = "Learn the ancient memory palace method to dramatically improve your recall for historical dates, scientific formulas, and vocabulary.",
            icon = Icons.Filled.Psychology,
            color = Color(0xFFE91E63),
            priority = "High",
            estimatedTime = "35 min",
            difficulty = "Advanced",
            category = "Study Techniques"
        ),
        AIRecommendation(
            title = "Active Reading Strategy",
            description = "Implement the SQ3R method (Survey, Question, Read, Recite, Review) to improve comprehension and retention of textbook material.",
            icon = Icons.Filled.MenuBook,
            color = Color(0xFF00BCD4),
            priority = "Medium",
            estimatedTime = "40 min",
            difficulty = "Beginner",
            category = "Reading"
        ),
        AIRecommendation(
            title = "Pomodoro Technique",
            description = "Break your study sessions into 25-minute focused intervals with 5-minute breaks. This technique can boost concentration and prevent burnout.",
            icon = Icons.Filled.Timer,
            color = Color(0xFFFF5722),
            priority = "High",
            estimatedTime = "25 min",
            difficulty = "Beginner",
            category = "Time Management"
        ),
        AIRecommendation(
            title = "Mind Mapping",
            description = "Create visual mind maps to connect related concepts and improve understanding of complex topics. Great for visual learners!",
            icon = Icons.Filled.Hub,
            color = Color(0xFF795548),
            priority = "Medium",
            estimatedTime = "30 min",
            difficulty = "Intermediate",
            category = "Visual Learning"
        )
    )

    return allRecommendations.shuffled().take(Random.nextInt(4, 7))
}
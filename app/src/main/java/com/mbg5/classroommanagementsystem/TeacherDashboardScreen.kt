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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.ClassroomResponse
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun TeacherDashboardScreen(
    navController: NavHostController,
    viewModel: TeacherViewModel = viewModel(),
    quizViewModel: QuizViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val meId = SessionManager.currentUserId
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(meId) {
        if (meId != null) {
            viewModel.fetchTeacher(meId)
        } else {
            navController.navigate("login") {
                popUpTo("teacherDashboard") { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF667eea)
                    )
                )
            )
    ) {
        TeacherBackgroundParticles()

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                TeacherState.Loading -> {
                    LoadingAnimation()
                }
                is TeacherState.DetailLoaded -> {
                    val t = (state as TeacherState.DetailLoaded).teacher

                    val allClasses by produceState(
                        initialValue = emptyList<ClassroomResponse>(),
                        t.uid
                    ) {
                        value = runCatching { ApiClient.apiService.listClasses().body().orEmpty() }
                            .getOrDefault(emptyList())
                    }

                    val myClasses = allClasses.filter { it.teacher.id == t.uid }

                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(800, easing = FastOutSlowInEasing)
                        )
                    ) {
                        val scrollState = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp)
                        ) {
                            WelcomeHeader(teacherName = t.fullName)

                            Spacer(Modifier.height(20.dp))

                            TeacherInfoCard(
                                subject = t.subjectExpertise,
                                qualification = t.qualification,
                                experience = t.yearsOfExperience,
                                email = t.email,
                                phone = t.phone
                            )

                            Spacer(Modifier.height(24.dp))

                            if (myClasses.isEmpty()) {
                                EmptyClassesCard()
                            } else {
                                Text(
                                    "Your Classes:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                )
                                Spacer(Modifier.height(12.dp))

                                myClasses.forEachIndexed { index, cls ->
                                    AnimatedVisibility(
                                        visible = showContent,
                                        enter = slideInVertically(
                                            initialOffsetY = { it },
                                            animationSpec = tween(
                                                durationMillis = 600,
                                                delayMillis = index * 100,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 600,
                                                delayMillis = index * 100
                                            )
                                        )
                                    ) {
                                        FixedEnhancedClassCard(
                                            cls = cls,
                                            onDetailsClick = { navController.navigate("classDetails/${cls.id}") },
                                            onGradebookClick = { navController.navigate("gradebook/${cls.id}") },
                                            onCreateQuizClick = { navController.navigate("createQuiz/${cls.id}") },
                                            onAttendanceClick = { navController.navigate("attendance/${cls.id}") }
                                        )
                                    }

                                    if (index < myClasses.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }

                            Spacer(Modifier.height(32.dp))

                            // Add Student Button - New Addition
                            Button(
                                onClick = { navController.navigate("addStudent") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(25.dp),
                                        ambientColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Add Student",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Add Student",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Leave Management Button
                            Button(
                                onClick = { navController.navigate("teacherLeave") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(25.dp),
                                        ambientColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3)
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Leave Management",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Leave Management",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            EnhancedLogoutButton(
                                onClick = {
                                    SessionManager.logout()
                                    navController.navigate("login") {
                                        popUpTo("teacherDashboard") { inclusive = true }
                                    }
                                }
                            )

                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
                is TeacherState.Error -> {
                    ErrorCard(message = (state as TeacherState.Error).message)
                }
                else -> { /* Idle */ }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(teacherName: String) {
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
            val infiniteTransition = rememberInfiniteTransition(label = "avatar_glow")
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
                                Color(0xFF667eea).copy(alpha = glowAlpha),
                                Color(0xFF764ba2).copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "Teacher",
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Hello, $teacherName",
                    style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                )
                Text(
                    "Ready to inspire minds today?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
            }
        }
    }
}

@Composable
private fun TeacherInfoCard(
    subject: String,
    qualification: String,
    experience: Int,
    email: String,
    phone: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.White.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InfoRow(
                icon = Icons.Outlined.Subject,
                label = "Subject",
                value = subject,
                color = Color(0xFF4CAF50)
            )
            InfoRow(
                icon = Icons.Outlined.School,
                label = "Qualification",
                value = qualification,
                color = Color(0xFF2196F3)
            )
            InfoRow(
                icon = Icons.Outlined.WorkHistory,
                label = "Experience",
                value = "$experience yrs",
                color = Color(0xFFFF9800)
            )
            InfoRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = email,
                color = Color(0xFF9C27B0)
            )
            InfoRow(
                icon = Icons.Outlined.Phone,
                label = "Phone",
                value = phone,
                color = Color(0xFFE91E63),
                isLast = true
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = color
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "$label: $value",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF1A1A2E),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    if (!isLast) {
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun FixedEnhancedClassCard(
    cls: ClassroomResponse,
    onDetailsClick: () -> Unit,
    onGradebookClick: () -> Unit,
    onCreateQuizClick: () -> Unit,
    onAttendanceClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF667eea).copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667eea).copy(alpha = 0.3f),
                                    Color(0xFF764ba2).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Class,
                        contentDescription = "Class",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF667eea)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        cls.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${cls.students.size} student${if (cls.students.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF666666)
                        )
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF666666)
                )
            }

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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = "Students",
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Students:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (cls.students.isEmpty()) {
                        Text(
                            "No students enrolled yet",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF888888),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            modifier = Modifier.padding(start = 28.dp)
                        )
                    } else {
                        cls.students.forEach { s ->
                            Row(
                                Modifier.padding(start = 28.dp, top = 2.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF667eea))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    s.fullName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF444444)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onDetailsClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Details",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Details",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            Button(
                                onClick = onGradebookClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Grade,
                                    contentDescription = "Gradebook",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Gradebook",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onCreateQuizClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Quiz,
                                    contentDescription = "Create Quiz",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Create Quiz",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            Button(
                                onClick = onAttendanceClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF9C27B0)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Face,
                                    contentDescription = "AI Attendance",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "AI Attendance",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyClassesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.White.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF667eea).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Class,
                    contentDescription = "No Classes",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFF667eea)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "You aren't teaching any classes yet.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF1A1A2E),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun EnhancedLogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(25.dp),
                ambientColor = Color.Red.copy(alpha = 0.2f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5252)
        ),
        shape = RoundedCornerShape(25.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Logout,
            contentDescription = "Logout",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Log Out",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun LoadingAnimation() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(60.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.White,
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF667eea))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Loading...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Red.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Error",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TeacherBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(25) {
            TeacherParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                speed = Random.nextFloat() * 0.03f + 0.01f,
                color = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.05f),
                    Color(0xFF667eea).copy(alpha = 0.1f),
                    Color(0xFF764ba2).copy(alpha = 0.1f)
                ).random(),
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "teacher_particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
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

private data class TeacherParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val phase: Float
)
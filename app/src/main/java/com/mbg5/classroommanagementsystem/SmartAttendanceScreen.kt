package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

data class StudentAttendance(
    val id: String,
    val name: String,
    val isPresent: Boolean,
    val confidence: Float,
    val timeStamp: Long,
    val mood: String = "Neutral"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAttendanceScreen(
    navController: NavHostController
) {
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }
    var currentStudent by remember { mutableStateOf<StudentAttendance?>(null) }
    var attendanceList by remember { mutableStateOf(listOf<StudentAttendance>()) }
    var showAnalytics by remember { mutableStateOf(false) }
    var aiThinking by remember { mutableStateOf(false) }
    var insights by remember { mutableStateOf(listOf<String>()) }

    // Simulated student data
    val simulatedStudents = remember {
        listOf(
            "Nimra", "Memoona", "Sameena", "Aleena",
            "Minahil", "Nida", "Marwa", "Husan",
            "Zahra", "Ayesha", "Noor", "Sehrish"
        )
    }

    // AI scanning simulation
    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanProgress = 0f
            for (i in 0..100) {
                delay(50)
                scanProgress = i / 100f
                
                if (i % 20 == 0 && i > 0) {
                    val studentName = simulatedStudents.random()
                    val isPresent = Random.nextBoolean()
                    val confidence = Random.nextFloat() * 0.3f + 0.7f
                    val mood = listOf("Happy", "Neutral", "Focused", "Tired").random()
                    
                    currentStudent = StudentAttendance(
                        id = i.toString(),
                        name = studentName,
                        isPresent = isPresent,
                        confidence = confidence,
                        timeStamp = System.currentTimeMillis(),
                        mood = mood
                    )
                    
                    delay(1000)
                    
                    attendanceList = attendanceList + currentStudent!!
                    currentStudent = null
                }
            }
            isScanning = false
            showAnalytics = true
        }
    }

    // Generate AI insights
    LaunchedEffect(showAnalytics) {
        if (showAnalytics) {
            aiThinking = true
            delay(2000)
            insights = generateAIInsights(attendanceList)
            aiThinking = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            )
    ) {
        // Animated background particles
        AttendanceBackgroundParticles()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    imageVector = Icons.Filled.Face,
                                    contentDescription = "AI Attendance",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "AI Smart Attendance",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
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
            },
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Camera Interface
                item {
                    AICameraInterface(
                        isScanning = isScanning,
                        scanProgress = scanProgress,
                        currentStudent = currentStudent,
                        onStartScan = { isScanning = true }
                    )
                }

                // Attendance Statistics
                item {
                    AttendanceStatisticsCard(attendanceList)
                }

                // Behavioral Analytics
                if (showAnalytics) {
                    item {
                        BehavioralAnalyticsCard(
                            insights = insights,
                            aiThinking = aiThinking
                        )
                    }
                }

                // Attendance List
                items(attendanceList) { student ->
                    StudentAttendanceCard(student = student)
                }
            }
        }
    }
}

@Composable
private fun AICameraInterface(
    isScanning: Boolean,
    scanProgress: Float,
    currentStudent: StudentAttendance?,
    onStartScan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AI Camera Frame
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
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
                if (isScanning) {
                    // Scanning animation
                    val infiniteTransition = rememberInfiniteTransition(label = "scan")
                    val scanAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scan_alpha"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Scanning grid
                        for (i in 0..10) {
                            val y = size.height * i / 10
                            drawLine(
                                color = Color.White.copy(alpha = scanAlpha),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f
                            )
                        }
                    }

                    // Progress indicator
                    CircularProgressIndicator(
                        progress = scanProgress,
                        modifier = Modifier.size(80.dp),
                        color = Color.White,
                        strokeWidth = 8.dp
                    )

                    // AI Status
                    Text(
                        text = "AI Scanning...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Student Recognition
            currentStudent?.let { student ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (student.isPresent) 
                                Color(0xFF4CAF50).copy(alpha = 0.1f) 
                            else 
                                Color(0xFFF44336).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (student.isPresent) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = "Status",
                                tint = if (student.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Confidence: ${(student.confidence * 100).toInt()}% | Mood: ${student.mood}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Scan Button
            Button(
                onClick = onStartScan,
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start Scan",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanning) "Scanning..." else "Start AI Face Recognition",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatisticsCard(attendanceList: List<StudentAttendance>) {
    val totalStudents = attendanceList.size
    val presentCount = attendanceList.count { it.isPresent }
    val absentCount = totalStudents - presentCount
    val attendanceRate = if (totalStudents > 0) presentCount.toFloat() / totalStudents else 0f
    val averageConfidence = if (attendanceList.isNotEmpty()) 
        attendanceList.map { it.confidence }.average().toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Attendance Statistics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Present",
                    value = presentCount.toString(),
                    color = Color(0xFF4CAF50)
                )
                StatisticItem(
                    label = "Absent",
                    value = absentCount.toString(),
                    color = Color(0xFFF44336)
                )
                StatisticItem(
                    label = "Rate",
                    value = "${(attendanceRate * 100).toInt()}%",
                    color = Color(0xFF2196F3)
                )
                StatisticItem(
                    label = "AI Confidence",
                    value = "${(averageConfidence * 100).toInt()}%",
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun BehavioralAnalyticsCard(
    insights: List<String>,
    aiThinking: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = "AI Analytics",
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Behavioral Insights",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (aiThinking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF9C27B0),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI is analyzing behavioral patterns...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                insights.forEach { insight ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "Insight",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = insight,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentAttendanceCard(student: StudentAttendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (student.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                                if (student.isPresent) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Student",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Mood: ${student.mood} | Confidence: ${(student.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            // Status Indicator
            Icon(
                imageVector = if (student.isPresent) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = "Attendance Status",
                tint = if (student.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AttendanceBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(50) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1,
                speed = Random.nextFloat() * 1 + 0.2f,
                color = Color.White
            )
        }
    }
    val time by rememberInfiniteTransition(label = "attendance_particles").animateFloat(
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
                color = p.color.copy(alpha = 0.3f),
                radius = p.size * with(density) { 1.dp.toPx() },
                center = Offset(currentX, currentY)
            )
        }
    }
}

private fun generateAIInsights(attendanceList: List<StudentAttendance>): List<String> {
    val insights = mutableListOf<String>()
    
    if (attendanceList.isEmpty()) return insights
    
    val presentCount = attendanceList.count { it.isPresent }
    val totalCount = attendanceList.size
    val attendanceRate = presentCount.toFloat() / totalCount
    
    // Generate contextual insights
    when {
        attendanceRate >= 0.9f -> {
            insights.add("Excellent attendance rate! Students are highly engaged.")
            insights.add("Consider maintaining this momentum with positive reinforcement.")
        }
        attendanceRate >= 0.7f -> {
            insights.add("Good attendance pattern. Some students may need encouragement.")
            insights.add("Focus on building stronger connections with absent students.")
        }
        attendanceRate >= 0.5f -> {
            insights.add("Moderate attendance concerns detected.")
            insights.add("Consider implementing attendance incentives or parent communication.")
        }
        else -> {
            insights.add("Low attendance rate requires immediate attention.")
            insights.add("Recommend scheduling parent-teacher conferences.")
        }
    }
    
    // Mood analysis
    val moods = attendanceList.map { it.mood }
    val happyCount = moods.count { it == "Happy" }
    val tiredCount = moods.count { it == "Tired" }
    
    if (happyCount > totalCount / 2) {
        insights.add("Students appear to be in positive moods overall.")
    }
    if (tiredCount > totalCount / 3) {
        insights.add("Several students seem tired - consider adjusting class timing.")
    }
    
    // Confidence analysis
    val avgConfidence = attendanceList.map { it.confidence }.average()
    if (avgConfidence < 0.8) {
        insights.add("AI confidence is lower than usual. Check lighting conditions.")
    }
    
    return insights
}

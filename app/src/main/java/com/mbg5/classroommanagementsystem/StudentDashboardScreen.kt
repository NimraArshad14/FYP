package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
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
import com.mbg5.classroommanagementsystem.components.AnimatedDashboardButton

@Composable
fun StudentDashboardScreen(
    navController: NavHostController, // EXACT SAME
    viewModel: StudentViewModel = viewModel(), // EXACT SAME
    quizViewModel: QuizViewModel = viewModel() // EXACT SAME
) {
    val state by viewModel.state.collectAsState() // EXACT SAME
    val meId = SessionManager.currentUserId // EXACT SAME
    var isVisible by remember { mutableStateOf(false) }

    // EXACT SAME LaunchedEffect
    LaunchedEffect(meId) {
        if (meId != null) {
            viewModel.fetchStudent(meId)
        } else {
            navController.navigate("login") {
                popUpTo("studentDashboard") { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460),
                        Color(0xFF533483)
                    )
                )
            )
    ) {
        // Enhanced animated background
        EnhancedBackgroundEffect()

        Box(
            Modifier.fillMaxSize(), // EXACT SAME
            contentAlignment = Alignment.Center // EXACT SAME
        ) {
            // EXACT SAME when statement
            when (state) {
                StudentState.Loading -> {
                    // Premium loading design
                    Card(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                ambientColor = Color(0xFF6C63FF).copy(alpha = 0.3f),
                                spotColor = Color(0xFF6C63FF).copy(alpha = 0.3f)
                            ),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(
                                                Color(0xFF6C63FF),
                                                Color(0xFF3F51B5),
                                                Color(0xFF9C27B0),
                                                Color(0xFF6C63FF)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator( // EXACT SAME
                                    modifier = Modifier.size(40.dp),
                                    color = Color.White,
                                    strokeWidth = 4.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "Loading Dashboard...",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1A1A2E),
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
                is StudentState.DetailLoaded -> {
                    val stu = (state as StudentState.DetailLoaded).student // EXACT SAME

                    // EXACT SAME produceState
                    val allClasses by produceState(
                        initialValue = emptyList<ClassroomResponse>(),
                        stu.id
                    ) {
                        value = runCatching { ApiClient.apiService.listClasses().body().orEmpty() }
                            .getOrDefault(emptyList())
                    }

                    // EXACT SAME filter
                    val myClasses = allClasses.filter { cr ->
                        cr.students.any { it.id == stu.id }
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(1000, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(1000))
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize() // EXACT SAME
                                .padding(20.dp), // Enhanced padding
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                // Premium student profile card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 20.dp,
                                            shape = RoundedCornerShape(28.dp),
                                            ambientColor = Color(0xFF6C63FF).copy(alpha = 0.2f),
                                            spotColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
                                        ),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Box {
                                        // Subtle gradient overlay
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFF6C63FF),
                                                            Color(0xFF3F51B5),
                                                            Color(0xFF9C27B0)
                                                        )
                                                    )
                                                )
                                        )

                                        Column(
                                            modifier = Modifier.padding(28.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Premium avatar design
                                                Box(
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .shadow(
                                                            elevation = 12.dp,
                                                            shape = CircleShape,
                                                            ambientColor = Color(0xFF6C63FF).copy(alpha = 0.3f)
                                                        )
                                                        .clip(CircleShape)
                                                        .background(
                                                            brush = Brush.radialGradient(
                                                                colors = listOf(
                                                                    Color(0xFF6C63FF),
                                                                    Color(0xFF3F51B5)
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Person,
                                                        contentDescription = "Student",
                                                        modifier = Modifier.size(40.dp),
                                                        tint = Color.White
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(20.dp))

                                                Column {
                                                    Text(
                                                        "Welcome Back!",
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            color = Color(0xFF6C63FF),
                                                            fontWeight = FontWeight.Medium,
                                                            fontSize = 16.sp,
                                                            letterSpacing = 0.5.sp
                                                        )
                                                    )
                                                    Text(
                                                        "${stu.fullName}", // EXACT SAME TEXT
                                                        style = MaterialTheme.typography.headlineMedium.copy( // EXACT SAME STYLE BASE
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF1A1A2E),
                                                            fontSize = 24.sp,
                                                            letterSpacing = 0.3.sp
                                                        )
                                                    )
                                                    Text(
                                                        "Student Dashboard",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF666666),
                                                            fontSize = 14.sp
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(24.dp))

                                            // Premium contact info design
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFF8F9FF)
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(20.dp),
                                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Email,
                                                            contentDescription = "Email",
                                                            tint = Color(0xFF6C63FF),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Column {
                                                            Text(
                                                                "Email Address",
                                                                style = MaterialTheme.typography.bodySmall.copy(
                                                                    color = Color(0xFF666666),
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 12.sp
                                                                )
                                                            )
                                                            Text(
                                                                "${stu.email}", // EXACT SAME
                                                                style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                    color = Color(0xFF1A1A2E),
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 15.sp
                                                                )
                                                            )
                                                        }
                                                    }

                                                    Divider(
                                                        color = Color(0xFFE0E0E0),
                                                        thickness = 1.dp
                                                    )

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Phone,
                                                            contentDescription = "Phone",
                                                            tint = Color(0xFF6C63FF),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Column {
                                                            Text(
                                                                "Phone Number",
                                                                style = MaterialTheme.typography.bodySmall.copy(
                                                                    color = Color(0xFF666666),
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 12.sp
                                                                )
                                                            )
                                                            Text(
                                                                "${stu.phone}", // EXACT SAME
                                                                style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                    color = Color(0xFF1A1A2E),
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 15.sp
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

                            item {
                                // AI Smart Quiz Button
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 16.dp,
                                            shape = RoundedCornerShape(24.dp),
                                            ambientColor = Color(0xFF00D4FF).copy(alpha = 0.1f)
                                        )
                                        .clickable { navController.navigate("smartQuiz") },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF00D4FF).copy(alpha = 0.9f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
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
                                                contentDescription = "AI Quiz",
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                "AI Smart Quiz",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                "Try adaptive, AI-generated quizzes!",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.White.copy(alpha = 0.8f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                // EXACT SAME if-else logic
                                if (myClasses.isEmpty()) {
                                    // Premium empty state design
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 16.dp,
                                                shape = RoundedCornerShape(24.dp),
                                                ambientColor = Color(0xFFFF6B6B).copy(alpha = 0.1f)
                                            ),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(40.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        brush = Brush.radialGradient(
                                                            colors = listOf(
                                                                Color(0xFFFF6B6B).copy(alpha = 0.1f),
                                                                Color(0xFFFF6B6B).copy(alpha = 0.05f)
                                                            )
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.School,
                                                    contentDescription = "No Classes",
                                                    modifier = Modifier.size(50.dp),
                                                    tint = Color(0xFFFF6B6B)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(
                                                "No Classes Yet",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1A1A2E),
                                                    fontSize = 20.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "You are not enrolled in any classes yet.", // EXACT SAME TEXT
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color(0xFF666666),
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 16.sp,
                                                    lineHeight = 24.sp
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    // Premium classes section
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 16.dp,
                                                shape = RoundedCornerShape(24.dp),
                                                ambientColor = Color(0xFF6C63FF).copy(alpha = 0.1f)
                                            ),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            // Premium section header
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            brush = Brush.radialGradient(
                                                                colors = listOf(
                                                                    Color(0xFF6C63FF),
                                                                    Color(0xFF3F51B5)
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Class,
                                                        contentDescription = "Classes",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column {
                                                    Text(
                                                        "Your Classes", // EXACT SAME TEXT
                                                        style = MaterialTheme.typography.headlineSmall.copy( // EXACT SAME STYLE BASE
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF1A1A2E),
                                                            fontSize = 22.sp
                                                        )
                                                    )
                                                    Text(
                                                        "${myClasses.size} ${if (myClasses.size == 1) "class" else "classes"} enrolled",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF666666),
                                                            fontSize = 14.sp
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(24.dp))

                                            // Premium class cards
                                            myClasses.forEachIndexed { index, cls -> // EXACT SAME LOGIC
                                                if (index > 0) {
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                }

                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(
                                                            elevation = 8.dp,
                                                            shape = RoundedCornerShape(20.dp),
                                                            ambientColor = Color(0xFF6C63FF).copy(alpha = 0.05f)
                                                        ),
                                                    shape = RoundedCornerShape(20.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color(0xFFFAFAFF)
                                                    )
                                                ) {
                                                    Column(Modifier.padding(20.dp)) {
                                                        // Class header with premium design
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
                                                                                Color(0xFF4CAF50),
                                                                                Color(0xFF2196F3)
                                                                            )
                                                                        )
                                                                    ),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Filled.MenuBook,
                                                                    contentDescription = "Class",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(24.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(16.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    cls.name, // EXACT SAME
                                                                    style = MaterialTheme.typography.titleLarge.copy( // EXACT SAME STYLE BASE
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = Color(0xFF1A1A2E),
                                                                        fontSize = 18.sp
                                                                    )
                                                                )
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Icon(
                                                                        imageVector = Icons.Outlined.Person,
                                                                        contentDescription = "Teacher",
                                                                        tint = Color(0xFF6C63FF),
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        "Prof. ${cls.teacher.fullName}", // EXACT SAME
                                                                        style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                            color = Color(0xFF666666),
                                                                            fontSize = 14.sp,
                                                                            fontWeight = FontWeight.Medium
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        Spacer(Modifier.height(20.dp))

                                                        // Premium classmates section
                                                        Card(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(12.dp),
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = Color.White
                                                            )
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.padding(16.dp)
                                                            ) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Icon(
                                                                        imageVector = Icons.Outlined.Group,
                                                                        contentDescription = "Students",
                                                                        tint = Color(0xFF6C63FF),
                                                                        modifier = Modifier.size(18.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text(
                                                                        "Fellow Students:", // EXACT SAME
                                                                        style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                            fontWeight = FontWeight.SemiBold,
                                                                            color = Color(0xFF1A1A2E),
                                                                            fontSize = 14.sp
                                                                        )
                                                                    )
                                                                }

                                                                Spacer(modifier = Modifier.height(12.dp))

                                                                // EXACT SAME peers logic
                                                                val peers = cls.students
                                                                    .filterNot { it.id == stu.id }
                                                                    .map { it.fullName }

                                                                if (peers.isEmpty()) { // EXACT SAME
                                                                    Text(
                                                                        "(none)", // EXACT SAME
                                                                        style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                            color = Color(0xFF999999),
                                                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                                        )
                                                                    )
                                                                } else {
                                                                    peers.forEach { name -> // EXACT SAME
                                                                        Row(
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            modifier = Modifier.padding(vertical = 4.dp)
                                                                        ) {
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .size(24.dp)
                                                                                    .clip(CircleShape)
                                                                                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                                                                                contentAlignment = Alignment.Center
                                                                            ) {
                                                                                Icon(
                                                                                    imageVector = Icons.Filled.Person,
                                                                                    contentDescription = "Student",
                                                                                    tint = Color(0xFF4CAF50),
                                                                                    modifier = Modifier.size(14.dp)
                                                                                )
                                                                            }
                                                                            Spacer(modifier = Modifier.width(10.dp))
                                                                            Text(
                                                                                name, // EXACT SAME
                                                                                style = MaterialTheme.typography.bodyMedium.copy( // EXACT SAME STYLE BASE
                                                                                    color = Color(0xFF333333),
                                                                                    fontSize = 14.sp
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        Spacer(Modifier.height(20.dp))

                                                        // FIXED: Premium action buttons with proper horizontal layout
                                                        Column(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            // First row - Details and Grades
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                            ) {
                                                                Button(
                                                                    onClick = { navController.navigate("classDetails/${cls.id}") }, // EXACT SAME
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(48.dp), // Fixed height
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = Color(0xFF6C63FF)
                                                                    ),
                                                                    shape = RoundedCornerShape(16.dp),
                                                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                                                ) {
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        horizontalArrangement = Arrangement.Center
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Outlined.Visibility,
                                                                            contentDescription = "View",
                                                                            modifier = Modifier.size(18.dp),
                                                                            tint = Color.White
                                                                        )
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text(
                                                                            "Details", // EXACT SAME TEXT
                                                                            fontSize = 14.sp,
                                                                            fontWeight = FontWeight.SemiBold,
                                                                            color = Color.White,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                    }
                                                                }

                                                                Button(
                                                                    onClick = { navController.navigate("myGrades/${cls.id}") }, // EXACT SAME
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(48.dp), // Fixed height
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = Color(0xFF4CAF50)
                                                                    ),
                                                                    shape = RoundedCornerShape(16.dp),
                                                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                                                ) {
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        horizontalArrangement = Arrangement.Center
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Outlined.Grade,
                                                                            contentDescription = "Grades",
                                                                            modifier = Modifier.size(18.dp),
                                                                            tint = Color.White
                                                                        )
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text(
                                                                            "Grades", // EXACT SAME TEXT
                                                                            fontSize = 14.sp,
                                                                            fontWeight = FontWeight.SemiBold,
                                                                            color = Color.White,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                            // Second row - Quizzes (full width)
                                                            Button(
                                                                onClick = { navController.navigate("quizzes/${cls.id}") }, // EXACT SAME
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(48.dp), // Fixed height
                                                                colors = ButtonDefaults.buttonColors(
                                                                    containerColor = Color(0xFFFF9800)
                                                                ),
                                                                shape = RoundedCornerShape(16.dp),
                                                                elevation = ButtonDefaults.buttonElevation(4.dp)
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Outlined.Quiz,
                                                                        contentDescription = "Quizzes",
                                                                        modifier = Modifier.size(18.dp),
                                                                        tint = Color.White
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text(
                                                                        "Take Quizzes", // EXACT SAME TEXT
                                                                        fontSize = 14.sp,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        color = Color.White,
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                // Premium recommendations button
                                Button(
                                    onClick = { navController.navigate("learningRecommendations") }, // EXACT SAME
                                    modifier = Modifier
                                        .fillMaxWidth() // EXACT SAME
                                        .height(64.dp)
                                        .shadow(
                                            elevation = 12.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            ambientColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
                                        ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF9C27B0)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Psychology,
                                        contentDescription = "Recommendations",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "View Learning Recommendations", // EXACT SAME TEXT
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }

                            item {
                                // Premium logout button
                                Button(
                                    onClick = { // EXACT SAME onClick
                                        SessionManager.logout()
                                        navController.navigate("login") {
                                            popUpTo("studentDashboard") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth() // EXACT SAME
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF5252)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Logout,
                                        contentDescription = "Logout",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Log Out", // EXACT SAME TEXT
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }

                            item {
                                AnimatedDashboardButton(
                                    text = "📅 View Schedule",
                                    onClick = { navController.navigate("viewSchedule") },
                                    icon = Icons.Default.Description,
                                    color = Color(0xFF6C63FF),
                                    delay = 950
                                )
                            }

                            item {
                                AnimatedDashboardButton(
                                    text = "📝 My Complaints",
                                    onClick = { 
                                        navController.navigate("studentComplaints")
                                    },
                                    icon = Icons.Default.Report,
                                    color = Color(0xFF9C27B0),
                                    delay = 1000
                                )
                            }
                            item {
                                AnimatedDashboardButton(
                                    text = "🏖️ Leave Management",
                                    onClick = { 
                                        navController.navigate("studentLeave")
                                    },
                                    icon = Icons.Default.Event,
                                    color = Color(0xFF4CAF50),
                                    delay = 1200
                                )
                            }
                        }
                    }
                }
                is StudentState.Error -> {
                    // Premium error design
                    Card(
                        modifier = Modifier
                            .padding(20.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0xFFFF5252).copy(alpha = 0.2f)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFF5252).copy(alpha = 0.1f),
                                                Color(0xFFFF5252).copy(alpha = 0.05f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Oops! Something went wrong",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A2E)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                (state as StudentState.Error).message, // EXACT SAME
                                color = MaterialTheme.colorScheme.error, // EXACT SAME
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
                else -> { /* Idle */ } // EXACT SAME
            }
        }
    }
}

@Composable
private fun EnhancedBackgroundEffect() {
    val density = LocalDensity.current
    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.15f + 0.05f,
                color = listOf(
                    Color(0x15FFFFFF),
                    Color(0x106C63FF),
                    Color(0x159C27B0),
                    Color(0x104CAF50)
                ).random()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.x + time * particle.speed) % 1f * size.width
            val y = (particle.y + sin(time * 3f + particle.x * 8f) * 0.15f) * size.height

            drawCircle(
                color = particle.color,
                radius = particle.size * density.density,
                center = Offset(x, y)
            )
        }
    }
}


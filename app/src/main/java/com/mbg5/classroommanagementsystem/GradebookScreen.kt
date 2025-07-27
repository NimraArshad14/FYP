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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.GradeResponse
import com.mbg5.classroommanagementsystem.network.StudentProfileResponse
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradebookScreen(
    classId: String,
    navController: NavHostController,
    vm: GradeViewModel = viewModel()
) {
    // ORIGINAL CORE FUNCTIONALITY - UNCHANGED
    val state by vm.state.collectAsState()
    val roster = vm.roster
    var showContent by remember { mutableStateOf(false) }

    // ORIGINAL CORE LOGIC - UNCHANGED
    LaunchedEffect(classId) {
        vm.fetchClassData(classId)
    }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // ORIGINAL STATE VARIABLES - UNCHANGED
    var editing by remember { mutableStateOf<GradeResponse?>(null) }
    var show by remember { mutableStateOf(false) }

    // ORIGINAL DIALOG LOGIC - UNCHANGED
    if (show && editing != null) {
        EnhancedGradeEditDialog(
            roster = roster,
            grade = editing!!,
            onDismiss = { show = false },
            onSave = { sid, value, comment ->
                if (editing!!.id.isBlank()) {
                    vm.createGrade(classId, sid, value, comment)
                } else {
                    vm.updateGrade(classId, editing!!.id, sid, value, comment)
                }
                show = false
            }
        )
    }

    // Enhanced UI Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1e3c72),
                        Color(0xFF2a5298),
                        Color(0xFF1e3c72)
                    )
                )
            )
    ) {
        // Animated background particles
        GradebookBackgroundParticles()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                EnhancedTopAppBar(
                    title = "Gradebook",
                    onBackClick = { navController.navigateUp() }
                )
            },
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { padding ->
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
            ) {
                Column(Modifier.padding(padding)) {
                    when (state) {
                        GradeUiState.Loading -> {
                            // Enhanced Loading UI
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EnhancedLoadingAnimation()
                            }
                        }

                        is GradeUiState.ClassList -> {
                            // ORIGINAL GRADES LOGIC - UNCHANGED
                            val grades = (state as GradeUiState.ClassList).grades

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Enhanced Header Stats
                                GradebookStatsHeader(grades = grades)

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    itemsIndexed(grades) { index, g ->
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
                                            // Enhanced Grade Card with ORIGINAL FUNCTIONALITY
                                            EnhancedGradeCard(
                                                grade = g,
                                                roster = roster,
                                                onClick = {
                                                    // ORIGINAL CLICK LOGIC - UNCHANGED
                                                    editing = g
                                                    show = true
                                                }
                                            )
                                        }
                                    }

                                    item {
                                        Spacer(Modifier.height(16.dp))
                                        // Enhanced Add Grade Button with ORIGINAL FUNCTIONALITY
                                        EnhancedAddGradeButton(
                                            onClick = {
                                                // ORIGINAL ADD GRADE LOGIC - UNCHANGED
                                                editing = GradeResponse(
                                                    id = "",
                                                    classId = classId,
                                                    studentId = roster.firstOrNull()?.id.orEmpty(),
                                                    teacherId = SessionManager.currentUserId!!,
                                                    value = "",
                                                    comment = null,
                                                    timestamp = 0L
                                                )
                                                show = true
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }

                        is GradeUiState.Error -> {
                            // Enhanced Error UI
                            EnhancedErrorCard(
                                message = (state as GradeUiState.Error).msg
                            )
                        }

                        else -> { /* Idle */ }
                    }
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
                                    Color(0xFF4CAF50),
                                    Color(0xFF2E7D32)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Grade,
                        contentDescription = "Gradebook",
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
private fun GradebookStatsHeader(grades: List<GradeResponse>) {
    val totalGrades = grades.size
    val averageGrade = if (grades.isNotEmpty()) {
        grades.mapNotNull { it.value.toDoubleOrNull() }.average()
    } else 0.0
    val highestGrade = grades.mapNotNull { it.value.toDoubleOrNull() }.maxOrNull() ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = "Stats",
                    tint = Color(0xFF1e3c72),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Class Overview",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Outlined.Assignment,
                    label = "Total Grades",
                    value = totalGrades.toString(),
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    icon = Icons.Outlined.TrendingUp,
                    label = "Average",
                    value = String.format("%.1f", averageGrade),
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    icon = Icons.Outlined.Star,
                    label = "Highest",
                    value = String.format("%.1f", highestGrade),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF666666)
            )
        )
    }
}

@Composable
private fun EnhancedGradeCard(
    grade: GradeResponse,
    roster: List<StudentProfileResponse>,
    onClick: () -> Unit
) {
    val studentName = roster.find { it.id == grade.studentId }?.fullName ?: "Unknown Student"
    val gradeValue = grade.value.toDoubleOrNull()
    val gradeColor = when {
        gradeValue == null -> Color(0xFF666666)
        gradeValue >= 90 -> Color(0xFF4CAF50)
        gradeValue >= 80 -> Color(0xFF8BC34A)
        gradeValue >= 70 -> Color(0xFFFF9800)
        gradeValue >= 60 -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = gradeColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
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
                                Color(0xFF1e3c72).copy(alpha = 0.3f),
                                Color(0xFF2a5298).copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Student",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF1e3c72)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Student Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    studentName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (grade.comment?.isNotEmpty() == true) {
                    Text(
                        grade.comment,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF666666)
                        )
                    )
                }
            }

            // Grade Display
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(gradeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    grade.value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = gradeColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF666666),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EnhancedAddGradeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
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
            "Add Grade",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedGradeEditDialog(
    roster: List<StudentProfileResponse>,
    grade: GradeResponse,
    onDismiss: () -> Unit,
    onSave: (studentId: String, value: String, comment: String?) -> Unit
) {
    // ORIGINAL STATE VARIABLES - UNCHANGED
    var selected by remember { mutableStateOf(grade.studentId) }
    var expanded by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(grade.value) }
    var comment by remember { mutableStateOf(grade.comment.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (grade.id.isBlank()) Icons.Filled.Add else Icons.Filled.Edit,
                        contentDescription = "Grade",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    if (grade.id.isBlank()) "New Grade" else "Edit Grade",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ORIGINAL DROPDOWN LOGIC - UNCHANGED
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = roster.find { it.id == selected }?.fullName
                            ?: "Select student",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Student") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Student",
                                tint = Color(0xFF1e3c72)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1e3c72),
                            focusedLabelColor = Color(0xFF1e3c72)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roster.forEach { stu ->
                            DropdownMenuItem(
                                text = { Text(stu.fullName) },
                                onClick = {
                                    selected = stu.id
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Student",
                                        tint = Color(0xFF666666)
                                    )
                                }
                            )
                        }
                    }
                }

                // ORIGINAL VALUE FIELD - UNCHANGED
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Grade Value") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Grade,
                            contentDescription = "Grade",
                            tint = Color(0xFF4CAF50)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        focusedLabelColor = Color(0xFF4CAF50)
                    )
                )

                // ORIGINAL COMMENT FIELD - UNCHANGED
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (Optional)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "Comment",
                            tint = Color(0xFFFF9800)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800)
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // ORIGINAL SAVE LOGIC - UNCHANGED
                    onSave(selected, value, comment.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Save",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF666666)
                )
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun EnhancedLoadingAnimation() {
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
                    .background(Color(0xFF1e3c72))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Loading grades...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun EnhancedErrorCard(message: String) {
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
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Red
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
                message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun GradebookBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(20) {
            GradebookParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                color = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.05f),
                    Color(0xFF4CAF50).copy(alpha = 0.1f),
                    Color(0xFF2196F3).copy(alpha = 0.1f)
                ).random(),
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gradebook_particles")
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
            val y = (particle.y + sin(time * 2f + particle.phase) * 0.1f) * size.height

            drawCircle(
                color = particle.color,
                radius = particle.size * density.density,
                center = Offset(x, y)
            )
        }
    }
}

private data class GradebookParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val phase: Float
)
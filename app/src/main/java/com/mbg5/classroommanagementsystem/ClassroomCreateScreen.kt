package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.StudentResponse
import com.mbg5.classroommanagementsystem.network.TeacherResponse
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomCreateScreen(
    navController: NavHostController,
    vm: ClassroomViewModel = viewModel()
) {
    val createState by vm.createState.collectAsState() // SAME STATE
    val teachers by vm.teachers.collectAsState() // SAME TEACHERS
    val students by vm.students.collectAsState() // SAME STUDENTS
    var isVisible by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") } // SAME NAME STATE

    var expandedTeacher by remember { mutableStateOf(false) } // SAME TEACHER DROPDOWN STATE
    var selectedTeacher by remember { mutableStateOf<TeacherResponse?>(null) } // SAME TEACHER SELECTION

    var expandedStudents by remember { mutableStateOf(false) } // SAME STUDENTS DROPDOWN STATE
    val selectedStudents = remember { mutableStateListOf<StudentResponse>() } // SAME STUDENTS SELECTION

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // EXACT SAME NAVIGATION LOGIC
    LaunchedEffect(createState) {
        if (createState is CreateClassState.Success) {
            vm.resetCreate()
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb)
                    )
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles()

        // Enhanced Scaffold but SAME STRUCTURE
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Enhanced TopAppBar but SAME CONTENT
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
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
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "New Classroom",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "New Classroom", // SAME TITLE
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF333333)
                                    )
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            },
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { padding ->
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp), // SAME PADDING
                        verticalArrangement = Arrangement.spacedBy(20.dp) // Enhanced spacing
                    ) {
                        // Enhanced Class Name Field
                        AnimatedTextField(
                            value = name, // SAME VALUE
                            onValueChange = { name = it }, // SAME CHANGE HANDLER
                            label = "Class Name", // SAME LABEL
                            leadingIcon = Icons.Default.School,
                            delay = 200
                        )

                        // Enhanced Teacher Dropdown but SAME LOGIC
                        AnimatedDropdownSection(
                            label = "Select Teacher",
                            icon = Icons.Default.Person,
                            delay = 400
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedTeacher, // SAME EXPANDED STATE
                                onExpandedChange = { expandedTeacher = !expandedTeacher } // SAME CHANGE HANDLER
                            ) {
                                OutlinedTextField(
                                    value = selectedTeacher?.fullName.orEmpty(), // SAME VALUE LOGIC
                                    onValueChange = {}, // SAME HANDLER
                                    readOnly = true, // SAME READONLY
                                    label = { Text("Teacher") }, // SAME LABEL
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Teacher",
                                            tint = Color(0xFF667eea)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher) // SAME TRAILING ICON
                                    },
                                    modifier = Modifier
                                        .menuAnchor() // SAME MODIFIER
                                        .fillMaxWidth(), // SAME MODIFIER
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF667eea),
                                        focusedLabelColor = Color(0xFF667eea),
                                        cursorColor = Color(0xFF667eea)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedTeacher, // SAME EXPANDED
                                    onDismissRequest = { expandedTeacher = false } // SAME DISMISS
                                ) {
                                    teachers.forEach { t -> // SAME FOREACH LOGIC
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = "Teacher",
                                                        tint = Color(0xFF4CAF50),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(t.fullName) // SAME TEXT
                                                }
                                            },
                                            onClick = { // SAME CLICK LOGIC
                                                selectedTeacher = t
                                                expandedTeacher = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Enhanced Students Multi-select but SAME LOGIC
                        AnimatedDropdownSection(
                            label = "Select Students",
                            icon = Icons.Default.Group,
                            delay = 600
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedStudents, // SAME EXPANDED STATE
                                onExpandedChange = { expandedStudents = !expandedStudents } // SAME CHANGE HANDLER
                            ) {
                                OutlinedTextField(
                                    value = if (selectedStudents.isEmpty()) "" // SAME VALUE LOGIC
                                    else selectedStudents.joinToString(", ") { it.fullName }, // SAME JOIN LOGIC
                                    onValueChange = {}, // SAME HANDLER
                                    readOnly = true, // SAME READONLY
                                    label = { Text("Students") }, // SAME LABEL
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Group,
                                            contentDescription = "Students",
                                            tint = Color(0xFF667eea)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudents) // SAME TRAILING ICON
                                    },
                                    modifier = Modifier
                                        .menuAnchor() // SAME MODIFIER
                                        .fillMaxWidth(), // SAME MODIFIER
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF667eea),
                                        focusedLabelColor = Color(0xFF667eea),
                                        cursorColor = Color(0xFF667eea)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedStudents, // SAME EXPANDED
                                    onDismissRequest = { expandedStudents = false } // SAME DISMISS
                                ) {
                                    students.forEach { s -> // SAME FOREACH LOGIC
                                        val isSel = s in selectedStudents // SAME SELECTION CHECK
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    Modifier.fillMaxWidth(), // SAME MODIFIER
                                                    horizontalArrangement = Arrangement.SpaceBetween, // SAME ARRANGEMENT
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Person,
                                                            contentDescription = "Student",
                                                            tint = Color(0xFF2196F3),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(s.fullName) // SAME TEXT
                                                    }
                                                    Checkbox(
                                                        checked = isSel, // SAME CHECKED STATE
                                                        onCheckedChange = null, // SAME HANDLER
                                                        colors = CheckboxDefaults.colors(
                                                            checkedColor = Color(0xFF4CAF50)
                                                        )
                                                    )
                                                }
                                            },
                                            onClick = { // SAME CLICK LOGIC
                                                if (isSel) selectedStudents.remove(s) else selectedStudents.add(s)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Enhanced State Display but SAME LOGIC
                        when (createState) { // SAME WHEN LOGIC
                            CreateClassState.Loading -> {
                                AnimatedLoadingCard()
                            }
                            is CreateClassState.Error -> {
                                AnimatedErrorCard(
                                    message = (createState as CreateClassState.Error).message // SAME ERROR MESSAGE
                                )
                            }
                            else -> {} // SAME ELSE
                        }

                        // Enhanced Button but SAME FUNCTIONALITY
                        AnimatedCreateButton(
                            onClick = { // SAME CLICK LOGIC
                                vm.createClass(
                                    name = name,
                                    teacherId = selectedTeacher?.id.orEmpty(),
                                    studentIds = selectedStudents.map { it.id }
                                )
                            },
                            enabled = name.isNotBlank() // SAME ENABLED LOGIC
                                    && selectedTeacher != null
                                    && selectedStudents.isNotEmpty()
                                    && createState != CreateClassState.Loading,
                            delay = 800
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    delay: Long
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textfield_scale"
    )

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label,
                tint = Color(0xFF667eea)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea)
        )
    )
}

@Composable
private fun AnimatedDropdownSection(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    delay: Long,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dropdown_scale"
    )

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    Column(modifier = Modifier.scale(scale)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            )
        }
        content()
    }
}

@Composable
private fun AnimatedLoadingCard() {
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = rotation },
                color = Color(0xFF1976D2),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Creating classroom...",
                color = Color(0xFF1976D2),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun AnimatedErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                message,
                color = Color(0xFFD32F2F),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AnimatedCreateButton(
    onClick: () -> Unit,
    enabled: Boolean,
    delay: Long
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Create Classroom",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color.White
        )
    }
}

@Composable
private fun AnimatedBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(15) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                color = listOf(
                    Color(0x22FFFFFF),
                    Color(0x22FFD700),
                    Color(0x22FF6B35)
                ).random()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.x + time * particle.speed) % 1f * size.width
            val y = (particle.y + sin(time * 2f + particle.x * 8f) * 0.1f) * size.height

            drawCircle(
                color = particle.color,
                radius = particle.size * density.density,
                center = Offset(x, y)
            )
        }
    }
}


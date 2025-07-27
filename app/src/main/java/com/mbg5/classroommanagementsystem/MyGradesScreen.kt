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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGradesScreen(
    classId: String, // EXACT SAME
    navController: NavHostController, // EXACT SAME
    vm: GradeViewModel = viewModel() // EXACT SAME
) {
    val state by vm.state.collectAsState() // EXACT SAME
    val meId = SessionManager.currentUserId // EXACT SAME
    var isVisible by remember { mutableStateOf(false) }

    // EXACT SAME LaunchedEffect
    LaunchedEffect(meId) {
        if (meId == null) {
            navController.navigate("login") {
                popUpTo("myGrades") { inclusive = true }
            }
        } else {
            vm.fetchMyGrades(classId, meId)
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
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Grade,
                                contentDescription = "Grades",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "My Grades", // EXACT SAME TEXT
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent,
            bottomBar = {
                SimpleBottomNavigation(navController = navController)
            }
        ) { padding ->
            Box(Modifier.padding(padding)) { // EXACT SAME
                // EXACT SAME when statement
                when (state) {
                    GradeUiState.Loading -> {
                        // Enhanced loading but same logic
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(600))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.95f)
                                    ),
                                    elevation = CardDefaults.cardElevation(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
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
                                            CircularProgressIndicator( // EXACT SAME
                                                color = Color.White,
                                                strokeWidth = 3.dp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Loading your grades...",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF333333)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is GradeUiState.ClassList -> {
                        val grades = (state as GradeUiState.ClassList).grades // EXACT SAME

                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(800, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(800))
                        ) {
                            // EXACT SAME if-else logic
                            if (grades.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White.copy(alpha = 0.95f)
                                        ),
                                        elevation = CardDefaults.cardElevation(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        brush = Brush.radialGradient(
                                                            colors = listOf(
                                                                Color(0xFFFF9800),
                                                                Color(0xFFFF5722)
                                                            )
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Assignment,
                                                    contentDescription = "No Grades",
                                                    modifier = Modifier.size(40.dp),
                                                    tint = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                "No grades yet", // EXACT SAME TEXT
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF333333)
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "Your grades will appear here once your teacher adds them",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF666666)
                                                ),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize() // EXACT SAME
                                        .padding(16.dp), // EXACT SAME
                                    verticalArrangement = Arrangement.spacedBy(12.dp) // Enhanced spacing
                                ) {
                                    // Header card
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.95f)
                                            ),
                                            elevation = CardDefaults.cardElevation(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(20.dp),
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
                                                        imageVector = Icons.Filled.Grade,
                                                        contentDescription = "Grades",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column {
                                                    Text(
                                                        "Your Academic Performance",
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF333333)
                                                        )
                                                    )
                                                    Text(
                                                        "${grades.size} grade${if (grades.size != 1) "s" else ""} recorded",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF666666)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    items(grades) { g -> // EXACT SAME
                                        var cardVisible by remember { mutableStateOf(false) }
                                        val scale by animateFloatAsState(
                                            targetValue = if (cardVisible) 1f else 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            label = "grade_card_scale"
                                        )

                                        LaunchedEffect(Unit) {
                                            delay(100)
                                            cardVisible = true
                                        }

                                        Card(
                                            Modifier
                                                .fillMaxWidth() // EXACT SAME
                                                .scale(scale),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.95f)
                                            ),
                                            elevation = CardDefaults.cardElevation(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(20.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Grade indicator
                                                Box(
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            brush = Brush.radialGradient(
                                                                colors = getGradeColors(g.value)
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = g.value, // EXACT SAME
                                                        style = MaterialTheme.typography.headlineSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.School,
                                                            contentDescription = "Grade",
                                                            tint = Color(0xFF4CAF50),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            "Grade: ${g.value}", // EXACT SAME TEXT
                                                            style = MaterialTheme.typography.titleMedium.copy( // Enhanced from bodyMedium
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF333333)
                                                            )
                                                        )
                                                    }

                                                    // EXACT SAME comment logic
                                                    g.comment?.let {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.Comment,
                                                                contentDescription = "Comment",
                                                                tint = Color(0xFF2196F3),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Column {
                                                                Text(
                                                                    "Teacher's Note:",
                                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                                        fontWeight = FontWeight.Medium,
                                                                        color = Color(0xFF666666)
                                                                    )
                                                                )
                                                                Text(
                                                                    it, // EXACT SAME COMMENT TEXT
                                                                    style = MaterialTheme.typography.bodyMedium.copy( // Enhanced from bodySmall
                                                                        color = Color(0xFF444444)
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
                            }
                        }
                    }

                    is GradeUiState.Error -> {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(600))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFEBEE)
                                    ),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(24.dp), // Enhanced padding
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFD32F2F)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Error,
                                                contentDescription = "Error",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                "Oops! Something went wrong",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFD32F2F)
                                                )
                                            )
                                            Text(
                                                text = (state as GradeUiState.Error).msg, // EXACT SAME
                                                color = MaterialTheme.colorScheme.error, // EXACT SAME
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> { /* Idle: nothing to show until we load */ } // EXACT SAME
                }
            }
        }
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
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.15f + 0.05f,
                color = listOf(
                    Color(0x22FFFFFF),
                    Color(0x224CAF50),
                    Color(0x222196F3)
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
            val y = (particle.y + sin(time * 2f + particle.x * 6f) * 0.1f) * size.height

            drawCircle(
                color = particle.color,
                radius = particle.size * density.density,
                center = Offset(x, y)
            )
        }
    }
}


private fun getGradeColors(grade: String): List<Color> {
    return when {
        grade.contains("A", ignoreCase = true) -> listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
        grade.contains("B", ignoreCase = true) -> listOf(Color(0xFF2196F3), Color(0xFF1565C0))
        grade.contains("C", ignoreCase = true) -> listOf(Color(0xFFFF9800), Color(0xFFE65100))
        grade.contains("D", ignoreCase = true) -> listOf(Color(0xFFFF5722), Color(0xFFD84315))
        grade.contains("F", ignoreCase = true) -> listOf(Color(0xFFE91E63), Color(0xFFC2185B))
        else -> listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2))
    }
}
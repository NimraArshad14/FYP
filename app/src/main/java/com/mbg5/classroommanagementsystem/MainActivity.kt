package com.mbg5.classroommanagementsystem

import DashboardScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.mbg5.classroommanagementsystem.ui.theme.ClassroomManagementSystemTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassroomManagementSystemTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    val start = when {
                        SessionManager.isLoggedIn() && SessionManager.isAdmin()   -> "dashboard"
                        SessionManager.isLoggedIn() && SessionManager.isTeacher() -> "teacherDashboard"
                        SessionManager.isLoggedIn() && SessionManager.isStudent() -> "studentDashboard"
                        else                                                      -> "welcome"
                    }

                    NavHost(
                        navController = nav,
                        startDestination = start,
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { 1000 },
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(500))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { -1000 },
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(500))
                        }
                    ) {
                        // Welcome screen for unauthenticated users
                        composable("welcome") {
                            WelcomeScreen(
                                onTeacherLogin = { nav.navigate("login") },
                                onAdminLogin = { nav.navigate("login") },
                                onStudentLogin = { nav.navigate("login") },
                                onSignup = { nav.navigate("signup") }
                            )
                        }

                        // Authentication screens
                        composable("login")  { LoginScreen(nav) }
                        composable("signup") { SignupScreen(nav) }

                        // Admin dashboard and related routes
                        composable("dashboard")        { DashboardScreen(nav) }
                        composable("addTeacher")       { TeacherRegistrationScreen(navController = nav) }
                        composable("addStudent")       { StudentRegistrationScreen(navController = nav) }
                        composable("classes")          { ClassroomListScreen(navController = nav) }
                        composable("classroomCreate")  { ClassroomCreateScreen(navController = nav) } // Added route
                        composable("adminStats")       { AdminStatsScreen(navController = nav) }
                        composable("adminScheduleUpload") {
                            AdminScheduleUploadScreen(navController = nav)
                        }
                        composable("feeManagement") {
                            FeeManagementScreen(navController = nav)
                        }
                        composable("adminComplaintManagement") {
                            AdminComplaintManagementScreen(navController = nav)
                        }
                        composable("studentComplaints") {
                            StudentComplaintScreen(navController = nav)
                        }
                        composable("studentLeave") {
                            StudentLeaveScreen(navController = nav)
                        }
                        composable("viewSchedule") {
                            StudentViewScheduleScreen(navController = nav)
                        }

                        // Teacher dashboard and related routes
                        composable("teacherDashboard") { TeacherDashboardScreen(nav) }
                        composable("teacherLeave") {
                            TeacherLeaveManagementScreen(navController = nav)
                        }
                        composable(
                            "gradebook/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            GradebookScreen(
                                classId       = back.arguments!!.getString("classId")!!,
                                navController = nav
                            )
                        }
                        composable(
                            "attendance/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            SmartAttendanceScreen(
                                navController = nav
                            )
                        }

                        // Student dashboard and related routes
                        composable("studentDashboard") { StudentDashboardScreen(nav) }
                        composable(
                            "smartQuiz"
                        ) {
                            SmartQuizScreen(navController = nav)
                        }
                        composable(
                            "myGrades/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            MyGradesScreen(
                                classId       = back.arguments!!.getString("classId")!!,
                                navController = nav
                            )
                        }
                        composable("learningRecommendations") {
                            LearningRecommendationsScreen(navController = nav)
                        }

                        // Quiz-related routes
                        composable(
                            "createQuiz/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            TeacherQuizCreationScreen(
                                classId       = back.arguments!!.getString("classId")!!,
                                navController = nav
                            )
                        }
                        composable(
                            "quizzes/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            StudentQuizListScreen(
                                classId       = back.arguments!!.getString("classId")!!,
                                navController = nav
                            )
                        }
                        composable(
                            "attemptQuiz/{quizId}",
                            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
                        ) { back ->
                            StudentQuizAttemptScreen(
                                quizId        = back.arguments!!.getString("quizId")!!,
                                navController = nav
                            )
                        }

                        // Class details screen
                        composable(
                            "classDetails/{classId}",
                            arguments = listOf(navArgument("classId") { type = NavType.StringType })
                        ) { back ->
                            ClassDetailsScreen(
                                classId       = back.arguments!!.getString("classId")!!,
                                navController = nav
                            )
                        }
                    }
                }
            }
        }
    }
}

// Placeholder for ClassroomCreateScreen (replace with your actual implementation)
@Composable
fun ClassroomCreateScreen(navController: androidx.navigation.NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Classroom Create Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun WelcomeScreen(
    onTeacherLogin: () -> Unit,
    onAdminLogin: () -> Unit,
    onStudentLogin: () -> Unit,
    onSignup: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
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
                        Color(0xFFf093fb)
                    )
                )
            )
    ) {
        AnimatedBackgroundParticles()

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(1000))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedLogo()
                Spacer(modifier = Modifier.height(32.dp))
                AnimatedTitle()
                Spacer(modifier = Modifier.height(48.dp))
                AnimatedButton(
                    text = "Teacher Login",
                    onClick = onTeacherLogin,
                    delay = 500,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(
                    text = "Admin Login",
                    onClick = onAdminLogin,
                    delay = 700,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(
                    text = "Student Login",
                    onClick = onStudentLogin,
                    delay = 900,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedButton(
                    text = "Sign Up",
                    onClick = onSignup,
                    delay = 1100,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
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
                        Color(0xFFFFD700),
                        Color(0xFFFF6B35),
                        Color(0xFFE91E63)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "School Logo",
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer { rotationZ = rotation },
            tint = Color.White
        )
    }
}

@Composable
private fun AnimatedTitle() {
    val text = "Welcome to Classroom Manager"
    var visibleChars by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in text.indices) {
            delay(50)
            visibleChars = i + 1
        }
    }

    Text(
        text = text.take(visibleChars),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.3f),
                offset = Offset(2f, 2f),
                blurRadius = 4f
            )
        )
    )
}

@Composable
private fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    delay: Long,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    modifier: Modifier = Modifier
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
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp)),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
private fun AnimatedBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(100) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4 + 2,
                speed = Random.nextFloat() * 2 + 0.5f,
                color = Color.White
            )
        }
    }
    val time by rememberInfiniteTransition(label = "time").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        for (p in particles) {
            val currentX = (p.x * width + p.speed * time * 50) % width
            val currentY = (p.y * height + p.speed * time * 50) % height

            drawCircle(
                color = p.color.copy(alpha = (sin(time * 2 * Math.PI.toFloat()) * 0.2f + 0.8f)),
                radius = p.size * with(density) { 1.dp.toPx() },
                center = Offset(currentX, currentY)
            )
        }
    }
}

@Composable
fun SimpleBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Button
            Button(
                onClick = {
                    val homeDestination = when {
                        SessionManager.isAdmin() -> "dashboard"
                        SessionManager.isTeacher() -> "teacherDashboard"
                        SessionManager.isStudent() -> "studentDashboard"
                        else -> "welcome"
                    }
                    navController.navigate(homeDestination) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Home",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Back Button
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Back",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}


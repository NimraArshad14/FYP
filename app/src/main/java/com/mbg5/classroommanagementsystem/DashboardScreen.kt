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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.Particle
import com.mbg5.classroommanagementsystem.SessionManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mbg5.classroommanagementsystem.AdminStatsViewModel
import com.mbg5.classroommanagementsystem.components.AnimatedDashboardButton

@Composable
fun DashboardScreen(
    navController: NavHostController
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
        // Animated background particles
        AnimatedBackgroundParticles()

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Enhanced welcome card
                AnimatedWelcomeCard(delay = 200)

                Spacer(Modifier.height(24.dp))

                // Main action buttons
                AnimatedDashboardButton(
                    text = "➕ Add Teacher",
                    onClick = { navController.navigate("addTeacher") },
                    icon = Icons.Default.PersonAdd,
                    color = Color(0xFF4CAF50),
                    delay = 400
                )

                Spacer(Modifier.height(12.dp))

                AnimatedDashboardButton(
                    text = "➕ Add Student",
                    onClick = { navController.navigate("addStudent") },
                    icon = Icons.Default.School,
                    color = Color(0xFF2196F3),
                    delay = 500
                )

                Spacer(Modifier.height(12.dp))

                AnimatedDashboardButton(
                    text = "📚 Manage Classes",
                    onClick = { navController.navigate("classes") },
                    icon = Icons.Default.Class,
                    color = Color(0xFFFF9800),
                    delay = 600
                )

                Spacer(Modifier.height(20.dp))

                // Management section
                Text(
                    "Management Tools",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                AnimatedDashboardButton(
                    text = "📊 Quick Stats",
                    onClick = { navController.navigate("adminStats") },
                    icon = Icons.Default.BarChart,
                    color = Color(0xFF6C63FF),
                    delay = 700
                )

                Spacer(Modifier.height(12.dp))

                AnimatedDashboardButton(
                    text = "📤 Upload Schedule",
                    onClick = { navController.navigate("adminScheduleUpload") },
                    icon = Icons.Default.CloudUpload,
                    color = Color(0xFF6C63FF),
                    delay = 750
                )

                Spacer(Modifier.height(12.dp))

                AnimatedDashboardButton(
                    text = "💰 Fee Management",
                    onClick = { navController.navigate("feeManagement") },
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50),
                    delay = 800
                )

                Spacer(Modifier.height(12.dp))

                AnimatedDashboardButton(
                    text = "📝 Complaint Management",
                    onClick = { navController.navigate("adminComplaintManagement") },
                    icon = Icons.Default.Report,
                    color = Color(0xFF9C27B0),
                    delay = 850
                )

                Spacer(Modifier.height(24.dp))

                // Logout button
                AnimatedDashboardButton(
                    text = "Log Out",
                    onClick = {
                        SessionManager.logout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    icon = Icons.Default.Logout,
                    color = Color(0xFFE91E63),
                    delay = 900
                )

                // Bottom padding for better scrolling experience
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AnimatedWelcomeCard(delay: Long) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "welcome_scale"
    )

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated admin icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFF6B35)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin",
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Welcome, Admin!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            )
            
            Text(
                "Manage your classroom efficiently",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF666666)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AnimatedDashboardButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
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
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(26.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = Color.White
        )
    }
}

@Composable
private fun AnimatedBackgroundParticles() {
    val density = LocalDensity.current
    val particles = remember {
        List(20) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 0.2f + 0.1f,
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
            animation = tween(15000, easing = LinearEasing),
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


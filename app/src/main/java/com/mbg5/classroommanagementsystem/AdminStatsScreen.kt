package com.mbg5.classroommanagementsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStatsScreen(
    navController: NavHostController,
    statsViewModel: AdminStatsViewModel = viewModel()
) {
    val stats by statsViewModel.stats.collectAsState()
    val loading by statsViewModel.loading.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        statsViewModel.fetchStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Stats", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2), Color(0xFFf093fb))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Admin Quick Stats", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Spacer(Modifier.height(24.dp))

                        if (loading) {
                            CircularProgressIndicator()
                        } else if (error != null) {
                            Text("Error: $error", color = Color.Red)
                        } else {
                            StatRow("Total Students", stats.studentCount, Icons.Default.School, Color(0xFF2196F3))
                            Spacer(Modifier.height(16.dp))
                            StatRow("Total Teachers", stats.teacherCount, Icons.Default.Person, Color(0xFF4CAF50))
                            Spacer(Modifier.height(16.dp))
                            StatRow("Total Classes", stats.classCount, Icons.Default.Class, Color(0xFFFF9800))
                        }

                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { statsViewModel.fetchStats() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            Spacer(Modifier.width(8.dp))
                            Text("Refresh Stats")
                        }
                    }
                }

                // Additional stats cards can be added here
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("System Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("• Monitor student and teacher registrations", fontSize = 14.sp)
                        Text("• Track classroom assignments", fontSize = 14.sp)
                        Text("• View real-time statistics", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
        Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
    }
} 
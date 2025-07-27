package com.mbg5.classroommanagementsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mbg5.classroommanagementsystem.network.TeacherResponse
import com.mbg5.classroommanagementsystem.network.StudentResponse
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.components.StatusChip
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mbg5.classroommanagementsystem.components.EmptyLeaveState
import java.text.ParseException
import com.google.firebase.Timestamp

fun String.toDateOrNull(): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(this)
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLeaveScreen(
    navController: NavHostController,
    viewModel: LeaveViewModel = viewModel()
) {
    val leaves by viewModel.leaves.collectAsState()
    val pendingLeaves by viewModel.pendingLeaves.collectAsState()
    val approvedLeaves by viewModel.approvedLeaves.collectAsState()
    val rejectedLeaves by viewModel.rejectedLeaves.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showAIGeneration by remember { mutableStateOf(false) }
    var aiGeneratedText by remember { mutableStateOf("") }
    var isGeneratingAI by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch student profile and teachers
    val studentId = SessionManager.currentUserId ?: ""
    var studentProfile by remember { mutableStateOf<StudentResponse?>(null) }
    var teachers by remember { mutableStateOf<List<TeacherResponse>>(emptyList()) }
    var profileLoading by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        if (studentId.isNotBlank()) {
            profileLoading = true
            try {
                val studentResp = ApiClient.apiService.getStudent(studentId)
                if (studentResp.isSuccessful) {
                    studentProfile = studentResp.body()
                }
                
                val teachersResp = ApiClient.apiService.listTeachers()
                if (teachersResp.isSuccessful) {
                    teachers = teachersResp.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                profileLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchLeavesByStudent(studentId)
    }

    // Show error or success Snackbars
    LaunchedEffect(error) {
        if (error != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error!!)
                viewModel.clearError()
            }
        }
    }
    
    LaunchedEffect(success) {
        if (success != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(success!!)
                viewModel.clearSuccess()
            }
            showLeaveDialog = false
            showAIGeneration = false
        }
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                            )
                        )
                        .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Leave Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showLeaveDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Apply Leave", tint = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5FA),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pending",
                    value = pendingLeaves.size.toString(),
                    icon = Icons.Default.Schedule,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Approved",
                    value = approvedLeaves.size.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Rejected",
                    value = rejectedLeaves.size.toString(),
                    icon = Icons.Default.Cancel,
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Pending") },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Approved") },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Rejected") },
                    icon = { Icon(Icons.Default.Cancel, contentDescription = null) }
                )
            }

            // Leave List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4CAF50))
                        }
                    }
                } else {
                    val currentLeaves = when (selectedTab) {
                        0 -> leaves
                        1 -> pendingLeaves
                        2 -> approvedLeaves
                        3 -> rejectedLeaves
                        else -> emptyList()
                    }
                    
                    if (currentLeaves.isEmpty()) {
                        item {
                            EmptyLeaveState()
                        }
                    } else {
                        items(currentLeaves, key = { it.id }) { leave ->
                            LeaveCard(leave = leave)
                        }
                    }
                }
            }
        }

        // Leave Application Dialog
        if (showLeaveDialog) {
            LeaveApplicationDialog(
                onDismiss = { showLeaveDialog = false },
                onSubmit = { leaveType, reason, startDate, endDate, numberOfDays, teacherId ->
                    val teacher = teachers.find { it.id == teacherId }
                    val teacherName = teacher?.fullName ?: "Unknown Teacher"
                    
                    viewModel.createLeave(
                        leaveType, reason, startDate, endDate, numberOfDays,
                        teacherId, studentId, studentProfile?.fullName ?: "",
                        studentProfile?.clazz ?: "", teacherName
                    )
                },
                teachers = teachers,
                onGenerateAI = { leaveType, reason, startDate, endDate, numberOfDays ->
                    showAIGeneration = true
                    isGeneratingAI = true
                    aiGeneratedText = ""
                    
                    // Simulate AI generation
                    coroutineScope.launch {
                        val dots = listOf(".", "..", "...")
                        repeat(10) { index ->
                            aiGeneratedText = "AI is generating your application${dots[index % 3]}"
                            delay(300)
                        }
                        
                        // Generate the actual application
                        val application = generateAIApplication(
                            leaveType, reason, startDate, endDate, numberOfDays,
                            studentProfile?.fullName ?: "", studentProfile?.clazz ?: ""
                        )
                        aiGeneratedText = application
                        isGeneratingAI = false
                    }
                }
            )
        }

        // AI Generation Dialog
        if (showAIGeneration) {
            AIGenerationDialog(
                generatedText = aiGeneratedText,
                isGenerating = isGeneratingAI,
                onDismiss = { showAIGeneration = false }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LeaveCard(leave: com.mbg5.classroommanagementsystem.network.LeaveResponse) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (leave.leaveType) {
                        "SICK" -> Icons.Default.LocalHospital
                        "PERSONAL" -> Icons.Default.Person
                        "EMERGENCY" -> Icons.Default.Emergency
                        else -> Icons.Default.Event
                    },
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        leave.leaveType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "To: ${leave.teacherName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                StatusChip(
                    text = leave.status,
                    color = when (leave.status) {
                        "PENDING" -> Color(0xFFFF9800)
                        "APPROVED" -> Color(0xFF4CAF50)
                        "REJECTED" -> Color(0xFFF44336)
                        else -> Color.Gray
                    },
                    icon = when (leave.status) {
                        "PENDING" -> Icons.Default.Schedule
                        "APPROVED" -> Icons.Default.CheckCircle
                        "REJECTED" -> Icons.Default.Cancel
                        else -> Icons.Default.Info
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                leave.reason,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                val startDate = leave.startDate.toDateOrNull()
                val endDate = leave.endDate.toDateOrNull()
                val startDateStr = if (startDate != null) dateFormat.format(startDate) else ""
                val endDateStr = if (endDate != null) dateFormat.format(endDate) else ""
                Text(
                    "$startDateStr - $endDateStr",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${leave.numberOfDays} day${if (leave.numberOfDays == 1) "" else "s"}",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
            
            if (!leave.teacherResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Teacher Response:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            leave.teacherResponse,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveApplicationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Date, Date, Int, String) -> Unit,
    teachers: List<TeacherResponse>,
    onGenerateAI: (String, String, Date, Date, Int) -> Unit
) {
    var leaveType by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var numberOfDays by remember { mutableStateOf(1) }
    var selectedTeacherId by remember { mutableStateOf("") }
    var expandedLeaveType by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }

    val leaveTypes = listOf("SICK", "PERSONAL", "EMERGENCY", "OTHER")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply for Leave", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Leave Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedLeaveType,
                    onExpandedChange = { expandedLeaveType = !expandedLeaveType }
                ) {
                    OutlinedTextField(
                        value = leaveType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Leave Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLeaveType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLeaveType,
                        onDismissRequest = { expandedLeaveType = false }
                    ) {
                        leaveTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    leaveType = type
                                    expandedLeaveType = false
                                }
                            )
                        }
                    }
                }

                // Reason
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Date Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("End Date") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Number of Days
                OutlinedTextField(
                    value = numberOfDays.toString(),
                    onValueChange = { numberOfDays = it.toIntOrNull() ?: 1 },
                    label = { Text("Number of Days") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Teacher Selection
                ExposedDropdownMenuBox(
                    expanded = expandedTeacher,
                    onExpandedChange = { expandedTeacher = !expandedTeacher }
                ) {
                    OutlinedTextField(
                        value = teachers.find { it.id == selectedTeacherId }?.fullName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Teacher") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTeacher,
                        onDismissRequest = { expandedTeacher = false }
                    ) {
                        teachers.forEach { teacher ->
                            DropdownMenuItem(
                                text = { Text(teacher.fullName) },
                                onClick = {
                                    selectedTeacherId = teacher.id
                                    expandedTeacher = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onGenerateAI(leaveType, reason, startDate, endDate, numberOfDays)
                    },
                    enabled = leaveType.isNotBlank() && reason.isNotBlank() && selectedTeacherId.isNotBlank()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Generate AI")
                }
                Button(
                    onClick = {
                        onSubmit(leaveType, reason, startDate, endDate, numberOfDays, selectedTeacherId)
                    },
                    enabled = leaveType.isNotBlank() && reason.isNotBlank() && selectedTeacherId.isNotBlank()
                ) {
                    Text("Submit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AIGenerationDialog(
    generatedText: String,
    isGenerating: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI Generated Application", fontWeight = FontWeight.Bold) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (isGenerating) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            generatedText,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    Text(
                        generatedText,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun generateAIApplication(
    leaveType: String,
    reason: String,
    startDate: Date,
    endDate: Date,
    numberOfDays: Int,
    studentName: String,
    studentClass: String
): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    return """
Subject: Application for $leaveType Leave

Dear Sir/Madam,

I hope this letter finds you well. I am writing to formally request $leaveType leave from ${dateFormat.format(startDate)} to ${dateFormat.format(endDate)} ($numberOfDays day${if (numberOfDays == 1) "" else "s"}).

Reason for Leave: $reason

I understand the importance of maintaining regular attendance and assure you that I will make every effort to catch up on any missed coursework and assignments during my absence. I will also ensure that any group projects or collaborative work are not affected by my leave.

I kindly request your approval for this leave application. I will be available for any urgent academic matters via email or phone if needed.

Thank you for considering my request. I look forward to your response.

Sincerely,
$studentName
Class: $studentClass

---
This application was generated with AI assistance to ensure proper formatting and clarity.
    """.trimIndent()
} 
package com.mbg5.classroommanagementsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mbg5.classroommanagementsystem.components.StatusChip
import com.mbg5.classroommanagementsystem.components.EmptyLeaveState
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import java.text.ParseException
import com.google.firebase.Timestamp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLeaveManagementScreen(
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
    var selectedLeave by remember { mutableStateOf<com.mbg5.classroommanagementsystem.network.LeaveResponse?>(null) }
    var showResponseDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch teacher's leave applications
    val teacherId = SessionManager.currentUserId ?: ""

    LaunchedEffect(Unit) {
        viewModel.fetchLeavesByTeacher(teacherId)
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
            showResponseDialog = false
            selectedLeave = null
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
                                listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
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
                contentColor = Color(0xFF2196F3)
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
                            CircularProgressIndicator(color = Color(0xFF2196F3))
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
                            TeacherLeaveCard(
                                leave = leave,
                                onRespond = {
                                    selectedLeave = leave
                                    showResponseDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Response Dialog
        if (showResponseDialog && selectedLeave != null) {
            LeaveResponseDialog(
                leave = selectedLeave!!,
                onDismiss = { 
                    showResponseDialog = false
                    selectedLeave = null
                },
                onSubmit = { status, response ->
                    viewModel.updateLeaveStatus(selectedLeave!!.id, status, response)
                }
            )
        }
    }
}

@Composable
fun TeacherLeaveCard(
    leave: com.mbg5.classroommanagementsystem.network.LeaveResponse,
    onRespond: () -> Unit
) {
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
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        leave.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "${leave.leaveType} - ${leave.studentClass}",
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
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(16.dp)
                )
                val startDate = leave.startDate.toDateOrNull()
                val endDate = leave.endDate.toDateOrNull()
                val startDateStr = if (startDate != null) dateFormat.format(startDate) else ""
                val endDateStr = if (endDate != null) dateFormat.format(endDate) else ""
                Text(
                    "$startDateStr - $endDateStr",
                    fontSize = 12.sp,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${leave.numberOfDays} day${if (leave.numberOfDays == 1) "" else "s"}",
                    fontSize = 12.sp,
                    color = Color(0xFF2196F3)
                )
            }
            
            if (leave.status == "PENDING") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRespond,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Icon(Icons.Default.Reply, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Respond")
                    }
                }
            }
            
            if (!leave.teacherResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Your Response:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            leave.teacherResponse,
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveResponseDialog(
    leave: com.mbg5.classroommanagementsystem.network.LeaveResponse,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var status by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var expandedStatus by remember { mutableStateOf(false) }

    val statuses = listOf("APPROVED", "REJECTED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Respond to Leave Application", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "From: ${leave.studentName} (${leave.studentClass})",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                
                Text(
                    "Leave Type: ${leave.leaveType}",
                    fontSize = 14.sp
                )
                
                Text(
                    "Reason: ${leave.reason}",
                    fontSize = 14.sp
                )
                
                Text(
                    "Duration: ${leave.numberOfDays} day${if (leave.numberOfDays == 1) "" else "s"}",
                    fontSize = 14.sp
                )
                
                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statuses.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    status = s
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                // Response
                OutlinedTextField(
                    value = response,
                    onValueChange = { response = it },
                    label = { Text("Response (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(status, response)
                },
                enabled = status.isNotBlank()
            ) {
                Text("Submit Response")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 
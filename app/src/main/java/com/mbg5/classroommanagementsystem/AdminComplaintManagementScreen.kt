package com.mbg5.classroommanagementsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.mbg5.classroommanagementsystem.components.EmptyComplaintState
import com.mbg5.classroommanagementsystem.components.StatCard
import com.mbg5.classroommanagementsystem.components.StatusChip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComplaintManagementScreen(
    navController: NavHostController,
    viewModel: AdminComplaintViewModel = viewModel()
) {
    val complaints by viewModel.complaints.collectAsState()
    val pendingComplaints by viewModel.pendingComplaints.collectAsState()
    val resolvedComplaints by viewModel.resolvedComplaints.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var selectedComplaint by remember { mutableStateOf<com.mbg5.classroommanagementsystem.network.ComplaintResponse?>(null) }
    var showResponseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllComplaints()
    }

    LaunchedEffect(success) {
        if (success != null) {
            showResponseDialog = false
            selectedComplaint = null
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
                                listOf(Color(0xFF6C63FF), Color(0xFF2196F3))
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
                            "Complaint Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stat Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total",
                        value = complaints.size.toString(),
                        icon = Icons.Default.Report,
                        gradient = Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF2196F3))),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending",
                        value = pendingComplaints.size.toString(),
                        icon = Icons.Default.Pending,
                        gradient = Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFF5722))),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Resolved",
                        value = resolvedComplaints.size.toString(),
                        icon = Icons.Default.CheckCircle,
                        gradient = Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF2196F3))),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF6C63FF),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("All", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.List, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Pending", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Pending, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Resolved", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                    )
                }
            }

            // Loading State
            if (loading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6C63FF))
                    }
                }
            }

            // Empty State
            if (!loading) {
                val currentComplaints = when (selectedTab) {
                    0 -> complaints
                    1 -> pendingComplaints
                    2 -> resolvedComplaints
                    else -> emptyList()
                }
                
                if (currentComplaints.isEmpty()) {
                    item {
                        EmptyComplaintState()
                    }
                } else {
                    // Complaint Items
                    items(currentComplaints, key = { it.id }) { complaint ->
                        AdminComplaintCard(
                            complaint = complaint,
                            onRespond = {
                                selectedComplaint = complaint
                                showResponseDialog = true
                            },
                            onDelete = {
                                viewModel.deleteComplaint(complaint.id)
                            }
                        )
                    }
                }
            }
        }

        // Response Dialog
        if (showResponseDialog && selectedComplaint != null) {
            ComplaintResponseDialog(
                complaint = selectedComplaint!!,
                onDismiss = { 
                    showResponseDialog = false
                    selectedComplaint = null
                },
                onSubmit = { status, response ->
                    viewModel.updateComplaintStatus(selectedComplaint!!.id, status, response)
                }
            )
        }
    }
}

@Composable
fun AdminComplaintCard(
    complaint: com.mbg5.classroommanagementsystem.network.ComplaintResponse,
    onRespond: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    tint = Color(0xFF6C63FF), 
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        complaint.studentName, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                    Text(
                        complaint.studentClass, 
                        fontSize = 12.sp, 
                        color = Color.Gray
                    )
                }
                StatusChip(
                    text = complaint.status,
                    color = when (complaint.status) {
                        "PENDING" -> Color(0xFFFF9800)
                        "IN_PROGRESS" -> Color(0xFF2196F3)
                        "RESOLVED" -> Color(0xFF4CAF50)
                        "REJECTED" -> Color(0xFFFF5722)
                        else -> Color.Gray
                    },
                    icon = when (complaint.status) {
                        "PENDING" -> Icons.Default.Pending
                        "IN_PROGRESS" -> Icons.Default.Schedule
                        "RESOLVED" -> Icons.Default.CheckCircle
                        "REJECTED" -> Icons.Default.Cancel
                        else -> Icons.Default.Info
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                complaint.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                complaint.description,
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Category, 
                    contentDescription = null, 
                    tint = Color(0xFF6C63FF), 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    complaint.category, 
                    fontSize = 12.sp, 
                    color = Color(0xFF6C63FF)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.PriorityHigh, 
                    contentDescription = null, 
                    tint = when (complaint.priority) {
                        "LOW" -> Color(0xFF4CAF50)
                        "MEDIUM" -> Color(0xFFFF9800)
                        "HIGH" -> Color(0xFFFF5722)
                        "URGENT" -> Color(0xFFE91E63)
                        else -> Color.Gray
                    }, 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    complaint.priority, 
                    fontSize = 12.sp, 
                    color = when (complaint.priority) {
                        "LOW" -> Color(0xFF4CAF50)
                        "MEDIUM" -> Color(0xFFFF9800)
                        "HIGH" -> Color(0xFFFF5722)
                        "URGENT" -> Color(0xFFE91E63)
                        else -> Color.Gray
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.Event, 
                    contentDescription = null, 
                    tint = Color(0xFF6C63FF), 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    dateFormat.format(complaint.createdAt), 
                    fontSize = 12.sp, 
                    color = Color(0xFF6C63FF)
                )
            }
            
            if (!complaint.adminResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Admin Response:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            complaint.adminResponse,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRespond,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Respond", fontSize = 12.sp)
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFEAEA), shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintResponseDialog(
    complaint: com.mbg5.classroommanagementsystem.network.ComplaintResponse,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var status by remember { mutableStateOf(complaint.status) }
    var response by remember { mutableStateOf(complaint.adminResponse ?: "") }
    var expandedStatus by remember { mutableStateOf(false) }

    val statuses = listOf("PENDING", "IN_PROGRESS", "RESOLVED", "REJECTED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Respond to Complaint", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "From: ${complaint.studentName} (${complaint.studentClass})",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                
                Text(
                    "Title: ${complaint.title}",
                    fontSize = 14.sp
                )
                
                Text(
                    "Description: ${complaint.description}",
                    fontSize = 14.sp
                )
                
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
                        statuses.forEach { stat ->
                            DropdownMenuItem(
                                text = { Text(stat) },
                                onClick = {
                                    status = stat
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = response,
                    onValueChange = { response = it },
                    label = { Text("Response") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (response.isNotBlank()) {
                        onSubmit(status, response)
                    }
                },
                enabled = response.isNotBlank()
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
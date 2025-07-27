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
import com.mbg5.classroommanagementsystem.SessionManager
import com.mbg5.classroommanagementsystem.network.ApiClient
import com.mbg5.classroommanagementsystem.network.StudentResponse
import android.util.Log
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentComplaintScreen(
    navController: NavHostController,
    viewModel: ComplaintViewModel = viewModel()
) {
    val complaints by viewModel.complaints.collectAsState()
    val pendingComplaints by viewModel.pendingComplaints.collectAsState()
    val resolvedComplaints by viewModel.resolvedComplaints.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showAddComplaintDialog by remember { mutableStateOf(false) }

    // Fetch student profile
    val studentId = SessionManager.currentUserId ?: ""
    var studentProfile by remember { mutableStateOf<StudentResponse?>(null) }
    var profileLoading by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(studentId) {
        if (studentId.isNotBlank()) {
            profileLoading = true
            profileError = null
            try {
                val resp = ApiClient.apiService.getStudent(studentId)
                if (resp.isSuccessful) {
                    studentProfile = resp.body()
                } else {
                    profileError = "Failed to load profile"
                }
            } catch (e: Exception) {
                profileError = e.localizedMessage ?: "Error loading profile"
            } finally {
                profileLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("StudentComplaintScreen", "Fetching complaints for student ID: $studentId")
        viewModel.fetchComplaints(studentId)
    }

    // Show error or success Snackbars
    LaunchedEffect(error) {
        if (error != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error!!)
                Log.e("StudentComplaintScreen", "Error: $error")
                viewModel.clearError()
            }
        }
    }
    LaunchedEffect(success) {
        if (success != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(success!!)
                Log.d("StudentComplaintScreen", "Success: $success")
                viewModel.clearSuccess()
            }
            showAddComplaintDialog = false
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
                            "My Complaints",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showAddComplaintDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Complaint", tint = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5FA),
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        ComplaintCard(complaint = complaint)
                    }
                }
            }
        }

        // Add Complaint Dialog
        if (showAddComplaintDialog) {
            AddComplaintDialog(
                onDismiss = { showAddComplaintDialog = false },
                onSubmit = { title, description, category, priority ->
                    val name = studentProfile?.fullName ?: ""
                    val clazz = studentProfile?.clazz ?: ""
                    Log.d("StudentComplaintScreen", "Submitting complaint: title=$title, desc=$description, cat=$category, pri=$priority, id=$studentId, name=$name, class=$clazz")
                    viewModel.createComplaint(title, description, category, priority, studentId, name, clazz)
                }
            )
        }
    }
}

@Composable
fun ComplaintCard(complaint: com.mbg5.classroommanagementsystem.network.ComplaintResponse) {
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
                    Icons.Default.Report, 
                    contentDescription = null, 
                    tint = Color(0xFF6C63FF), 
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        complaint.title, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                    Text(
                        complaint.category, 
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
                complaint.description,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Admin Response:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            complaint.adminResponse,
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
fun AddComplaintDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }

    val categories = listOf("ACADEMIC", "INFRASTRUCTURE", "BEHAVIOR", "OTHER")
    val priorities = listOf("LOW", "MEDIUM", "HIGH", "URGENT")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit New Complaint", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
                
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        priorities.forEach { pri ->
                            DropdownMenuItem(
                                text = { Text(pri) },
                                onClick = {
                                    priority = pri
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && priority.isNotBlank()) {
                        onSubmit(title, description, category, priority)
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && priority.isNotBlank()
            ) {
                Text("Submit Complaint")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 
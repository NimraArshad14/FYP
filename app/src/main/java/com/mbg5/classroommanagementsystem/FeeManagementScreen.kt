package com.mbg5.classroommanagementsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    navController: NavHostController,
    viewModel: FeeManagementViewModel = viewModel()
) {
    val fees by viewModel.fees.collectAsState()
    val students by viewModel.students.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val unpaidFees by viewModel.unpaidFees.collectAsState()
    val unverifiedFees by viewModel.unverifiedFees.collectAsState()
    
    var showAddFeeDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.fetchUnpaidFees()
        viewModel.fetchUnverifiedFees()
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
                            "Fee Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showAddFeeDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Fee", tint = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Stat Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Fees",
                    value = fees.size.toString(),
                    icon = Icons.Default.AttachMoney,
                    gradient = Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF2196F3))),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Unpaid",
                    value = unpaidFees.size.toString(),
                    icon = Icons.Default.Warning,
                    gradient = Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFF5722))),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Unverified",
                    value = unverifiedFees.size.toString(),
                    icon = Icons.Default.Pending,
                    gradient = Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF2196F3))),
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF6C63FF),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Fees", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Unpaid", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Unverified", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Pending, contentDescription = null) }
                )
            }

            // Content
            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6C63FF))
                    }
                }
                when(selectedTab) { 
                    0 -> fees.isEmpty(); 
                    1 -> unpaidFees.isEmpty(); 
                    2 -> unverifiedFees.isEmpty(); 
                    else -> true 
                } -> {
                    EmptyState()
                }
                else -> {
                    val currentFees = when (selectedTab) {
                        0 -> fees
                        1 -> unpaidFees
                        2 -> unverifiedFees
                        else -> emptyList()
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentFees, key = { it.id }) { fee ->
                            FeeCard(fee = fee, viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Error Dialog
        if (error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Error") },
                text = { Text(error!!) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                }
            )
        }

        // Add Fee Dialog
        if (showAddFeeDialog) {
            AddFeeDialog(
                students = students,
                onDismiss = { showAddFeeDialog = false },
                onAddFee = { studentId, amount, dueDate, notes ->
                    viewModel.createFee(studentId, amount, dueDate, notes)
                    showAddFeeDialog = false
                }
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradient: Brush, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient, shape = RoundedCornerShape(18.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
            Text(title, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF6C63FF), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("No fees found", fontWeight = FontWeight.Medium, color = Color(0xFF6C63FF), fontSize = 18.sp)
        Text("All clear! No fee records to show.", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun FeeCard(fee: com.mbg5.classroommanagementsystem.network.FeeResponse, viewModel: FeeManagementViewModel) {
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
                        fee.studentName, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                    Text(
                        fee.studentClass, 
                        fontSize = 12.sp, 
                        color = Color.Gray
                    )
                }
                StatusChip(
                    text = if (fee.isPaid) "Paid" else "Unpaid",
                    color = if (fee.isPaid) Color(0xFF4CAF50) else Color(0xFFFF5722),
                    icon = if (fee.isPaid) Icons.Default.CheckCircle else Icons.Default.Cancel
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AttachMoney, 
                    contentDescription = null, 
                    tint = Color(0xFF4CAF50), 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${String.format("%.2f", fee.amount)}", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp, 
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.Event, 
                    contentDescription = null, 
                    tint = Color(0xFF6C63FF), 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Due: ${dateFormat.format(fee.dueDate)}", 
                    fontSize = 12.sp, 
                    color = Color(0xFF6C63FF)
                )
            }
            
            if (!fee.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Note: ${fee.notes}", 
                    fontSize = 12.sp, 
                    color = Color(0xFF666666)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!fee.isPaid) {
                    Button(
                        onClick = { viewModel.markAsPaid(fee.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Paid", fontSize = 12.sp)
                    }
                }
                if (!fee.isVerified) {
                    Button(
                        onClick = { viewModel.markAsVerified(fee.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify", fontSize = 12.sp)
                    }
                }
                IconButton(
                    onClick = { viewModel.deleteFee(fee.id) },
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

@Composable
fun StatusChip(text: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = color, 
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text, 
                color = color, 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeeDialog(
    students: List<com.mbg5.classroommanagementsystem.network.StudentResponse>,
    onDismiss: () -> Unit,
    onAddFee: (String, Double, Date, String?) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf(Date()) }
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = dueDate,
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Fee", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Student Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = students.find { it.id == selectedStudentId }?.fullName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Student") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.fullName} (${student.clazz})") },
                                onClick = {
                                    selectedStudentId = student.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (PKR)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                // Due Date Picker
                OutlinedTextField(
                    value = dateFormat.format(dueDate),
                    onValueChange = {},
                    label = { Text("Due Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF6C63FF))
                    }
                )
                // Notes Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedStudentId.isNotEmpty() && amount.isNotEmpty()) {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        onAddFee(selectedStudentId, amountValue, dueDate, notes.takeIf { it.isNotEmpty() })
                    }
                },
                enabled = selectedStudentId.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Add Fee")
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
fun DatePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val picker = android.app.DatePickerDialog(
            context,
            { _, y, m, d ->
                val cal = Calendar.getInstance()
                cal.set(y, m, d)
                onDateSelected(cal.time)
            },
            year, month, day
        )
        picker.setOnCancelListener { onDismiss() }
        picker.show()
        onDispose { picker.dismiss() }
    }
} 
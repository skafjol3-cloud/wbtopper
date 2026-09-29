package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategory
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExamCategoryScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.examCategories.collectAsState()
    val mockTests by viewModel.mockTests.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<ExamCategory?>(null) }

    // Dialog state
    var formName by remember { mutableStateOf("") }
    var formCode by remember { mutableStateOf("") }
    var formYear by remember { mutableStateOf("2026") }
    var formGroup by remember { mutableStateOf("Nursing & Paramedical") }
    var formDescription by remember { mutableStateOf("") }
    var formSyllabus by remember { mutableStateOf("") }
    var formPrice by remember { mutableStateOf("199") }
    var formFreeCount by remember { mutableStateOf("1") }

    val filteredCategories = categories.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.categoryGroup.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Exam Category Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800
                )
                Text(
                    text = "${categories.size} Active Categories • Real-time Firestore Sync",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }

            Button(
                onClick = {
                    formName = ""
                    formCode = ""
                    formYear = "2026"
                    formGroup = "Nursing Entrance"
                    formDescription = ""
                    formSyllabus = ""
                    formPrice = "199"
                    formFreeCount = "1"
                    editingCategory = null
                    showCreateDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search exam categories by name, code or stream...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue700) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCategories) { cat ->
                val testCount = mockTests.count { it.examCode == cat.code }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (cat.isPublished) SuccessGreenLight else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (cat.isPublished) "PUBLISHED" else "DRAFT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (cat.isPublished) SuccessGreen else BreakingRed,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cat.code,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue700
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = RoyalBlue50,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Package: ₹${cat.packagePrice.toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalBlue800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${cat.name} (${cat.year})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )

                        Text(
                            text = cat.description,
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tests: $testCount Series • Free: ${cat.freeMockTestCount} Test",
                                fontSize = 11.sp,
                                color = TextMutedLight
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        editingCategory = cat
                                        formName = cat.name
                                        formCode = cat.code
                                        formYear = cat.year
                                        formGroup = cat.categoryGroup
                                        formDescription = cat.description
                                        formSyllabus = cat.syllabusSummary
                                        formPrice = cat.packagePrice.toInt().toString()
                                        formFreeCount = cat.freeMockTestCount.toString()
                                        showCreateDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue700, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { viewModel.adminToggleCategoryPublish(cat.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        if (cat.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        tint = if (cat.isPublished) TextSecondaryLight else SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.adminDeleteCategory(cat.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BreakingRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Category Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = if (editingCategory != null) "Edit Exam Category" else "Create New Exam Category",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Category Name (e.g. WBHRB Staff Nurse Grade II)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formCode,
                            onValueChange = { formCode = it },
                            label = { Text("Exam Code") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formYear,
                            onValueChange = { formYear = it },
                            label = { Text("Year") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formPrice,
                            onValueChange = { formPrice = it },
                            label = { Text("Package Price (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formFreeCount,
                            onValueChange = { formFreeCount = it },
                            label = { Text("Free Test Count") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = formGroup,
                        onValueChange = { formGroup = it },
                        label = { Text("Category Stream / Group") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formDescription,
                        onValueChange = { formDescription = it },
                        label = { Text("Description & Exam Overview") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formSyllabus,
                        onValueChange = { formSyllabus = it },
                        label = { Text("Syllabus & Subject Breakdown") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedPrice = formPrice.toDoubleOrNull() ?: 199.0
                        val parsedFree = formFreeCount.toIntOrNull() ?: 1
                        if (editingCategory != null) {
                            val updated = editingCategory!!.copy(
                                name = formName,
                                code = formCode.uppercase().trim(),
                                year = formYear,
                                categoryGroup = formGroup,
                                description = formDescription,
                                syllabusSummary = formSyllabus,
                                packagePrice = parsedPrice,
                                freeMockTestCount = parsedFree
                            )
                            viewModel.adminUpdateCategory(updated)
                        } else {
                            viewModel.adminCreateCategory(
                                name = formName,
                                code = formCode,
                                year = formYear,
                                group = formGroup,
                                description = formDescription,
                                syllabus = formSyllabus,
                                packagePrice = parsedPrice,
                                freeTestCount = parsedFree
                            )
                        }
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text(if (editingCategory != null) "Save Changes" else "Create Category")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

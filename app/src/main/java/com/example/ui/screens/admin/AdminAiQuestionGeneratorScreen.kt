package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.Question
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAiQuestionGeneratorScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.examCategories.collectAsState()
    val isGenerating by viewModel.isGeneratingAi.collectAsState()
    val progress by viewModel.aiProgress.collectAsState()
    val statusMsg by viewModel.aiStatusMessage.collectAsState()
    val previewQuestions by viewModel.previewQuestions.collectAsState()

    var selectedExamCode by remember { mutableStateOf("WBHRB_SN") }
    var selectedSubject by remember { mutableStateOf("Pharmacology & Critical Care") }
    var selectedTopic by remember { mutableStateOf("Emergency Drug Dilutions & Triage") }
    var selectedCount by remember { mutableStateOf(50) }
    var selectedDifficulty by remember { mutableStateOf("Moderate") }
    var customPrompt by remember { mutableStateOf("") }

    var showCreateTestModal by remember { mutableStateOf(false) }
    var newTestTitle by remember { mutableStateOf("") }
    var newTestDuration by remember { mutableStateOf("75") }

    val subjectOptions = listOf(
        "Pharmacology & Critical Care",
        "Anatomy & Physiology",
        "Medical-Surgical Nursing",
        "Community Health Nursing",
        "Child Health & Pediatrics",
        "Obstetrical & Gynecological Nursing",
        "Mental Health Nursing",
        "Microbiology & Pathology",
        "General Science & Biology",
        "Reasoning & General Knowledge"
    )

    val countOptions = listOf(10, 20, 30, 50, 100)
    val difficultyOptions = listOf("Easy", "Moderate", "Difficult", "Mixed")

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
                    text = "AI Question Generator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800
                )
                Text(
                    text = "Generate 50 or 100 clinical MCQs with Gemini 3.5 Flash & convert to test",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }

            Surface(
                color = if (isGenerating) Color(0xFFFEF3C7) else SuccessGreenLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isGenerating) Icons.Default.Autorenew else Icons.Default.Psychology,
                        contentDescription = null,
                        tint = if (isGenerating) Color(0xFFB45309) else SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGenerating) "AI Generating..." else "AI Ready",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGenerating) Color(0xFFB45309) else SuccessGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (previewQuestions.isEmpty()) {
            // Configuration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, SurfaceBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. Target Exam Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            val isSel = cat.code == selectedExamCode
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedExamCode = cat.code },
                                label = { Text(cat.code, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlue800,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("2. Target Subject", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(subjectOptions) { subj ->
                            val isSel = subj == selectedSubject
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlue800,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("3. Topic / Syllabus Sub-Area", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = selectedTopic,
                        onValueChange = { selectedTopic = it },
                        placeholder = { Text("e.g. Gastrointestinal Pharmacology, ECG Rhythm Analysis") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("4. Question Count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(countOptions) { c ->
                                    val isSel = c == selectedCount
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedCount = c },
                                        label = { Text("$c Qs", fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = RoyalBlue800,
                                            selectedLabelColor = SurfaceWhite
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("5. Difficulty", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(difficultyOptions) { d ->
                                    val isSel = d == selectedDifficulty
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedDifficulty = d },
                                        label = { Text(d, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = RoyalBlue800,
                                            selectedLabelColor = SurfaceWhite
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("6. Custom AI Instructions (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        placeholder = { Text("e.g. Include clinical scenario MCQs with patient vitals and drug dosages. Cite standard WHO/NTEP protocols.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isGenerating) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                                color = RoyalBlue800,
                                trackColor = RoyalBlue50
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = statusMsg,
                                fontSize = 12.sp,
                                color = RoyalBlue800,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.generateAiQuestions(
                                    examCode = selectedExamCode,
                                    subject = selectedSubject,
                                    topic = selectedTopic,
                                    count = selectedCount,
                                    difficulty = selectedDifficulty,
                                    customPrompt = customPrompt
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate $selectedCount Questions with AI", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Preview Generated Questions List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Generated Preview (${previewQuestions.size} MCQs)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.clearPreviewQuestions() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Clear", fontSize = 11.sp, color = BreakingRed)
                    }

                    Button(
                        onClick = { viewModel.savePreviewQuestionsToBank() },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Save to Bank", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            newTestTitle = "$selectedExamCode Master Mock Test (AI Generated)"
                            showCreateTestModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Mock Test", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(previewQuestions) { idx, q ->
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
                                Surface(
                                    color = RoyalBlue50,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Question #${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(q.difficulty, fontSize = 11.sp, color = TextMutedLight)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deletePreviewQuestion(idx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = BreakingRed, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(q.questionText, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)

                            Spacer(modifier = Modifier.height(8.dp))
                            q.options.forEachIndexed { optIdx, optText ->
                                val isCorrect = optIdx == q.correctOptionIndex
                                Surface(
                                    color = if (isCorrect) SuccessGreenLight else SurfaceCardLight,
                                    shape = RoundedCornerShape(6.dp),
                                    border = if (isCorrect) BorderStroke(1.dp, SuccessGreen) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${('A' + optIdx)}. $optText",
                                            fontSize = 12.sp,
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCorrect) SuccessGreen else TextPrimaryLight
                                        )
                                        if (isCorrect) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rationale: ${q.explanation}",
                                fontSize = 11.sp,
                                color = TextSecondaryLight,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal to create Mock Test from approved AI questions
    if (showCreateTestModal) {
        AlertDialog(
            onDismissRequest = { showCreateTestModal = false },
            title = { Text("Publish Mock Test", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Convert ${previewQuestions.size} AI-generated questions directly into a published Mock Test for $selectedExamCode.",
                        fontSize = 12.5.sp,
                        color = TextSecondaryLight
                    )

                    OutlinedTextField(
                        value = newTestTitle,
                        onValueChange = { newTestTitle = it },
                        label = { Text("Mock Test Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTestDuration,
                        onValueChange = { newTestDuration = it },
                        label = { Text("Duration (Minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = newTestDuration.toIntOrNull() ?: 60
                        viewModel.createMockTestFromGenerated(
                            title = newTestTitle,
                            examCode = selectedExamCode,
                            subject = selectedSubject,
                            topic = selectedTopic,
                            durationMinutes = duration,
                            isFree = false,
                            price = 199.0
                        )
                        showCreateTestModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Publish to App")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTestModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

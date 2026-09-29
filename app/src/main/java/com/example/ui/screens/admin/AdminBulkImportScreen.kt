package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.importer.BulkQuestionParser
import com.example.data.model.Question
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import java.util.UUID

enum class BulkImportMethod {
    PASTE_TEXT,
    CSV_DATA,
    RAPID_ANSWER_KEY
}

@Composable
fun AdminBulkImportScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.examCategories.collectAsState()
    val isBatchSaving by viewModel.isBatchSaving.collectAsState()
    val batchSaveProgress by viewModel.batchSaveProgress.collectAsState()
    val batchSaveStatusMessage by viewModel.batchSaveStatusMessage.collectAsState()
    val existingBank by viewModel.questionBank.collectAsState()

    var selectedMethod by remember { mutableStateOf(BulkImportMethod.PASTE_TEXT) }
    var selectedExamCode by remember { mutableStateOf("WBHRB_SN") }
    var selectedSubject by remember { mutableStateOf("Medical-Surgical Nursing") }
    var selectedTopic by remember { mutableStateOf("High-Yield Questions") }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var importDestination by remember { mutableStateOf("BANK") } // "BANK" or "MOCK_TEST"

    var rawInputText by remember { mutableStateOf("") }
    var rawCsvText by remember { mutableStateOf("") }

    // Parsed and validated questions ready for preview/editing
    val parsedQuestions = remember { mutableStateListOf<Question>() }
    val duplicateQuestions = remember { mutableStateListOf<Question>() }
    val parseErrors = remember { mutableStateListOf<String>() }
    var hasParsed by remember { mutableStateOf(false) }

    // Dialog state for editing a question in preview
    var editingQuestionIndex by remember { mutableStateOf<Int?>(null) }
    var editQText by remember { mutableStateOf("") }
    var editOptA by remember { mutableStateOf("") }
    var editOptB by remember { mutableStateOf("") }
    var editOptC by remember { mutableStateOf("") }
    var editOptD by remember { mutableStateOf("") }
    var editCorrectIndex by remember { mutableStateOf(0) }
    var editExplanation by remember { mutableStateOf("") }
    var editDifficulty by remember { mutableStateOf("Medium") }

    // Rapid Answer Key State
    val rapidAnswers = remember { mutableStateMapOf<Int, Int>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bulk Question Importer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800
                )
                Text(
                    text = "Import 50 or 100 MCQs • Duplicate Detection • Safe Batch Sync",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }

            Surface(
                color = RoyalBlue50,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Bank: ${existingBank.size} Qs",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Method Selector Tab Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedMethod == BulkImportMethod.PASTE_TEXT,
                onClick = { selectedMethod = BulkImportMethod.PASTE_TEXT },
                label = { Text("Method A: Text Paste", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue800,
                    selectedLabelColor = SurfaceWhite
                )
            )

            FilterChip(
                selected = selectedMethod == BulkImportMethod.CSV_DATA,
                onClick = { selectedMethod = BulkImportMethod.CSV_DATA },
                label = { Text("Method B: CSV Upload", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue800,
                    selectedLabelColor = SurfaceWhite
                )
            )

            FilterChip(
                selected = selectedMethod == BulkImportMethod.RAPID_ANSWER_KEY,
                onClick = { selectedMethod = BulkImportMethod.RAPID_ANSWER_KEY },
                label = { Text("Method C: Rapid Keys", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue800,
                    selectedLabelColor = SurfaceWhite
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, SurfaceBorderLight)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Target Exam Category & Storage Destination", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = importDestination == "BANK",
                            onClick = { importDestination = "BANK" }
                        )
                        Text("Question Bank + Cloud Firestore", fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = importDestination == "MOCK_TEST",
                            onClick = { importDestination = "MOCK_TEST" }
                        )
                        Text("New Mock Test Draft", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Content based on Selected Method
        when (selectedMethod) {
            BulkImportMethod.PASTE_TEXT -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Paste English MCQs (Four Options, Rationale, Difficulty)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Sample MCQ Buttons for 50 or 100 questions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    rawInputText = BulkQuestionParser.generatePresetQuestionsText(50, selectedExamCode)
                                    viewModel.showToast("Loaded 50 High-Yield Clinical MCQs Template")
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Load 50 Sample MCQs", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    rawInputText = BulkQuestionParser.generatePresetQuestionsText(100, selectedExamCode)
                                    viewModel.showToast("Loaded 100 Comprehensive MCQs Template")
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Load 100 Sample MCQs", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = { rawInputText = it },
                            placeholder = { Text("Q1. Question text here...\nA) Option A\nB) Option B\nC) Option C\nD) Option D\nAns: B\nExplanation: Clinically verified rationale...\nSubject: Medical-Surgical Nursing\nTopic: Cardiovascular System\nDifficulty: Medium\n\nQ2. Next question...") },
                            minLines = 8,
                            maxLines = 14,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val res = BulkQuestionParser.parseFormattedText(
                                    rawText = rawInputText,
                                    defaultSubject = selectedSubject,
                                    defaultTopic = selectedTopic,
                                    defaultExamCode = selectedExamCode,
                                    defaultDifficulty = selectedDifficulty,
                                    existingBank = existingBank
                                )
                                parsedQuestions.clear()
                                parsedQuestions.addAll(res.validQuestions)
                                duplicateQuestions.clear()
                                duplicateQuestions.addAll(res.duplicateQuestions)
                                parseErrors.clear()
                                parseErrors.addAll(res.errorRows)
                                hasParsed = true
                                viewModel.showToast("Parsed: ${res.validQuestions.size} valid, ${res.duplicateQuestions.size} duplicates detected.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Validate, Parse & Detect Duplicates", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            BulkImportMethod.CSV_DATA -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Paste or Upload CSV MCQs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Text("Header Format: Question,OptionA,OptionB,OptionC,OptionD,CorrectOption(A/B/C/D),Explanation,Subject,Topic,Difficulty", fontSize = 11.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rawCsvText,
                            onValueChange = { rawCsvText = it },
                            placeholder = { Text("Which hormone lowers blood glucose?,Insulin,Glucagon,Cortisol,Thyroxine,A,Insulin promotes cellular glucose uptake,Endocrinology,Pancreas,Easy") },
                            minLines = 6,
                            maxLines = 10,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val res = BulkQuestionParser.parseCsv(
                                    csvText = rawCsvText,
                                    defaultSubject = selectedSubject,
                                    defaultTopic = selectedTopic,
                                    defaultExamCode = selectedExamCode,
                                    existingBank = existingBank
                                )
                                parsedQuestions.clear()
                                parsedQuestions.addAll(res.validQuestions)
                                duplicateQuestions.clear()
                                duplicateQuestions.addAll(res.duplicateQuestions)
                                parseErrors.clear()
                                parseErrors.addAll(res.errorRows)
                                hasParsed = true
                                viewModel.showToast("Parsed CSV: ${res.validQuestions.size} valid, ${res.duplicateQuestions.size} duplicates.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Parse CSV & Check Duplicates", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            BulkImportMethod.RAPID_ANSWER_KEY -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Rapid Answer Key Builder (Fast 10-Question Blocks)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Text("Rapidly assign answers A/B/C/D to numbered examination stems", fontSize = 11.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(10.dp))

                        (1..10).forEach { qIdx ->
                            val chosen = rapidAnswers[qIdx] ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Q#$qIdx", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("A", "B", "C", "D").forEachIndexed { optIndex, optChar ->
                                        val isChosen = chosen == optIndex
                                        Surface(
                                            color = if (isChosen) RoyalBlue800 else RoyalBlue50,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clickable { rapidAnswers[qIdx] = optIndex }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = optChar,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isChosen) SurfaceWhite else TextPrimaryLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val generated = (1..10).map { qNum ->
                                    val chosen = rapidAnswers[qNum] ?: 0
                                    Question(
                                        id = "key_q_${UUID.randomUUID().toString().take(8)}",
                                        questionNumber = qNum,
                                        questionText = "Objective Examination Question #$qNum for $selectedExamCode.",
                                        options = listOf("Clinical Evaluation Option A", "Pharmacological Therapy Option B", "Diagnostic Assessment Option C", "Surgical Management Option D"),
                                        correctOptionIndex = chosen,
                                        explanation = "Standard verified rationale corresponding to Option ${('A' + chosen)}.",
                                        subject = selectedSubject,
                                        topic = selectedTopic,
                                        difficulty = "Medium",
                                        examCode = selectedExamCode,
                                        source = "Rapid Key Builder"
                                    )
                                }
                                parsedQuestions.clear()
                                parsedQuestions.addAll(generated)
                                duplicateQuestions.clear()
                                parseErrors.clear()
                                hasParsed = true
                                viewModel.showToast("Generated 10 questions with selected keys.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Load into Preview List", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Parse Results & Verification Breakdown
        if (hasParsed) {
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Validation & Quality Report", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue900)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = SuccessGreenLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${parsedQuestions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                Text("Approved Qs", fontSize = 10.sp, color = SuccessGreen)
                            }
                        }

                        Surface(
                            color = if (duplicateQuestions.isEmpty()) RoyalBlue50 else AccentAmberLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${duplicateQuestions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (duplicateQuestions.isEmpty()) RoyalBlue800 else AccentAmber)
                                Text("Duplicates Filtered", fontSize = 10.sp, color = TextSecondaryLight)
                            }
                        }

                        Surface(
                            color = if (parseErrors.isEmpty()) RoyalBlue50 else BreakingRedLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${parseErrors.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (parseErrors.isEmpty()) RoyalBlue800 else BreakingRed)
                                Text("Errors", fontSize = 10.sp, color = TextSecondaryLight)
                            }
                        }
                    }

                    if (parseErrors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        parseErrors.take(3).forEach { err ->
                            Text("• $err", fontSize = 11.sp, color = BreakingRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Batch Saving Progress Bar if saving is active
                    if (isBatchSaving) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(batchSaveStatusMessage, fontSize = 11.sp, color = RoyalBlue800, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { batchSaveProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = SuccessGreen,
                                trackColor = RoyalBlue50
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.saveBatchQuestionsToFirestore(
                                questions = parsedQuestions.toList(),
                                destination = importDestination,
                                onComplete = { success ->
                                    if (success) {
                                        parsedQuestions.clear()
                                        hasParsed = false
                                        rawInputText = ""
                                        rawCsvText = ""
                                    }
                                }
                            )
                        },
                        enabled = parsedQuestions.isNotEmpty() && !isBatchSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save ${parsedQuestions.size} Approved Questions to Firestore (Safe Batches)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive Preview & Edit List
            if (parsedQuestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text("Questions Preview (${parsedQuestions.size} items) — Tap card to edit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                Spacer(modifier = Modifier.height(8.dp))

                parsedQuestions.forEachIndexed { index, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                editingQuestionIndex = index
                                editQText = q.questionText
                                editOptA = q.options.getOrElse(0) { "" }
                                editOptB = q.options.getOrElse(1) { "" }
                                editOptC = q.options.getOrElse(2) { "" }
                                editOptD = q.options.getOrElse(3) { "" }
                                editCorrectIndex = q.correctOptionIndex
                                editExplanation = q.explanation
                                editDifficulty = q.difficulty
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, SurfaceBorderLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Q${index + 1}. ${q.subject}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = RoyalBlue50,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(q.difficulty, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { parsedQuestions.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BreakingRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(q.questionText, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Ans: Option ${('A' + q.correctOptionIndex)} (${q.options.getOrElse(q.correctOptionIndex) { "" }})", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            Text("Exp: ${q.explanation}", fontSize = 10.5.sp, color = TextSecondaryLight, maxLines = 2)
                        }
                    }
                }
            }
        }
    }

    // Question Edit Dialog
    if (editingQuestionIndex != null) {
        AlertDialog(
            onDismissRequest = { editingQuestionIndex = null },
            title = { Text("Edit Question #${editingQuestionIndex!! + 1}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = editQText,
                        onValueChange = { editQText = it },
                        label = { Text("Question Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = editOptA, onValueChange = { editOptA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = editOptB, onValueChange = { editOptB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = editOptC, onValueChange = { editOptC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = editOptD, onValueChange = { editOptD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Correct Option:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("A", "B", "C", "D").forEachIndexed { optIdx, optChar ->
                            FilterChip(
                                selected = editCorrectIndex == optIdx,
                                onClick = { editCorrectIndex = optIdx },
                                label = { Text(optChar) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editExplanation,
                        onValueChange = { editExplanation = it },
                        label = { Text("Explanation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = editingQuestionIndex!!
                        parsedQuestions[idx] = parsedQuestions[idx].copy(
                            questionText = editQText,
                            options = listOf(editOptA, editOptB, editOptC, editOptD),
                            correctOptionIndex = editCorrectIndex,
                            explanation = editExplanation,
                            difficulty = editDifficulty
                        )
                        editingQuestionIndex = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingQuestionIndex = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

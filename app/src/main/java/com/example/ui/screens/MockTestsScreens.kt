package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MockTest
import com.example.data.model.TestAttempt
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

@Composable
fun MockTestsScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val mockTests by viewModel.mockTests.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filterChips = listOf(
        "ALL" to "All Tests",
        "WBHRB_SN" to "WBHRB Staff Nurse",
        "ANM_GNM" to "ANM GNM",
        "NORCET_10" to "NORCET 10",
        "RRB_NURSE" to "RRB Superintendent",
        "WB_CHO" to "WB CHO",
        "JENPAS_UG" to "JENPAS-UG",
        "AIIMS_NURSE" to "AIIMS Nursing"
    )

    val filteredTests = if (selectedFilter == "ALL") {
        mockTests
    } else {
        mockTests.filter { it.examCode == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(bottom = 76.dp)
            .testTag("mock_tests_list_screen")
    ) {
        // Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterChips) { (code, label) ->
                val isSelected = selectedFilter == code
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = code },
                    label = {
                        Text(
                            label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue800,
                        selectedLabelColor = SurfaceWhite
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        if (filteredTests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Quiz, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No mock tests available in this category yet.", color = TextSecondaryLight, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTests) { test ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(Screen.TestInstructions(test.id)) }
                            .testTag("test_item_${test.id}"),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (test.isFree) SuccessGreenLight else RoyalBlue50,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (test.isFree) "FREE MOCK" else "PREMIUM • ₹${test.price.toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (test.isFree) SuccessGreen else RoyalBlue800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = "${test.attemptsCount} Attempts",
                                    fontSize = 11.sp,
                                    color = TextMutedLight
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = test.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.questions.size} Questions", fontSize = 12.sp, color = TextSecondaryLight)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.durationMinutes} Mins", fontSize = 12.sp, color = TextSecondaryLight)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.totalMarks.toInt()} Marks", fontSize = 12.sp, color = TextSecondaryLight)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.navigateTo(Screen.TestInstructions(test.id)) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "View Instructions & Start Test",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInstructionsScreen(
    testId: String,
    viewModel: ExamViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val mockTests by viewModel.mockTests.collectAsState()
    val test = mockTests.find { it.id == testId } ?: mockTests.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Instructions", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = SurfaceWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = test.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue800,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Test parameters summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue50),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Questions:", fontSize = 13.sp, color = TextSecondaryLight)
                        Text("${test.questions.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = RoyalBlue100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration:", fontSize = 13.sp, color = TextSecondaryLight)
                        Text("${test.durationMinutes} Minutes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = RoyalBlue100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Correct Marking:", fontSize = 13.sp, color = TextSecondaryLight)
                        Text("+${test.positiveMarks} Mark", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = RoyalBlue100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Negative Marking:", fontSize = 13.sp, color = TextSecondaryLight)
                        Text("-${test.negativeMarks} Mark", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BreakingRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "General Exam Instructions:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )

            Spacer(modifier = Modifier.height(10.dp))

            val instructions = listOf(
                "1. The test timer will commence as soon as you tap 'Start Test'. The paper will auto-submit when the countdown expires.",
                "2. Each question contains four options. Tap one option to mark your response.",
                "3. Use 'Mark for Review' to bookmark questions for second-pass review.",
                "4. You can freely navigate to any question at any time using the Question Palette icon.",
                "5. Upon submission, a comprehensive scorecard with accuracy and rationales will be generated."
            )

            instructions.forEach { instr ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = instr, fontSize = 13.sp, color = TextSecondaryLight, lineHeight = 19.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { viewModel.startTest(test.id) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_test_now_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I am Ready, Start Test",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTestScreen(
    testId: String,
    viewModel: ExamViewModel
) {
    val activeTest by viewModel.activeTest.collectAsState()
    val currentQIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val markedForReview by viewModel.markedForReview.collectAsState()
    val remainingSeconds by viewModel.remainingTimeSeconds.collectAsState()

    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        showSubmitConfirmDialog = true
    }

    val test = activeTest ?: return
    val currentQuestion = test.questions.getOrNull(currentQIndex) ?: return

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    Scaffold(
        topBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Q ${currentQIndex + 1}/${test.questions.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )

                    Surface(
                        color = if (remainingSeconds < 300) BreakingRedLight else RoyalBlue50,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (remainingSeconds < 300) BreakingRed else RoyalBlue100
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (remainingSeconds < 300) BreakingRed else RoyalBlue800,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingSeconds < 300) BreakingRed else RoyalBlue800
                            )
                        }
                    }

                    IconButton(onClick = { showPaletteSheet = true }) {
                        Icon(Icons.Default.GridView, contentDescription = "Question Palette", tint = RoyalBlue800)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearResponse(currentQIndex) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleMarkForReview(currentQIndex) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (markedForReview.contains(currentQIndex)) PurpleAccent else TextSecondaryLight
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (markedForReview.contains(currentQIndex)) "Marked ✓" else "Mark Review",
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { showSubmitConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BreakingRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("submit_test_button")
                        ) {
                            Text("Submit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { viewModel.previousQuestion() },
                            enabled = currentQIndex > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue50, contentColor = RoyalBlue800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("← Previous")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { viewModel.nextQuestion() },
                            enabled = currentQIndex < test.questions.size - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next →")
                        }
                    }
                }
            }
        },
        containerColor = SurfaceWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Surface(
                color = RoyalBlue50,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    text = "${currentQuestion.subject} • ${currentQuestion.topic}",
                    fontSize = 11.sp,
                    color = RoyalBlue800,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Text(
                text = currentQuestion.questionText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            val options = currentQuestion.options
            val selectedOption = selectedAnswers[currentQIndex]

            options.forEachIndexed { optIndex, optText ->
                val isSelected = selectedOption == optIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) RoyalBlue50 else SurfaceWhite)
                        .border(
                            1.5.dp,
                            if (isSelected) RoyalBlue800 else SurfaceBorderLight,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectOption(currentQIndex, optIndex) }
                        .padding(14.dp)
                        .testTag("test_option_${optIndex}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) RoyalBlue800 else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + optIndex).toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurfaceWhite else TextPrimaryLight
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optText,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = TextPrimaryLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Question Palette",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(SuccessGreen, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Answered", fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(PurpleAccent, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review", fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(Color(0xFFCBD5E1), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unanswered", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(test.questions.size) { qIdx ->
                        val isAnswered = selectedAnswers.containsKey(qIdx)
                        val isMarked = markedForReview.contains(qIdx)
                        val isCurrent = currentQIndex == qIdx

                        val tileColor = when {
                            isMarked -> PurpleAccent
                            isAnswered -> SuccessGreen
                            else -> Color(0xFFF1F5F9)
                        }

                        val textColor = when {
                            isMarked || isAnswered -> SurfaceWhite
                            else -> TextPrimaryLight
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(tileColor)
                                .border(if (isCurrent) 2.dp else 0.dp, RoyalBlue800, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.goToQuestion(qIdx)
                                    showPaletteSheet = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${qIdx + 1}",
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = {
                Text("Submit Mock Test?", fontWeight = FontWeight.Bold)
            },
            text = {
                val answeredCount = selectedAnswers.size
                val reviewCount = markedForReview.size
                val unattemptedCount = test.questions.size - answeredCount
                Column {
                    Text("Are you sure you want to finalize and submit this test? Detailed score analysis and full rationales will be generated immediately.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• Answered: $answeredCount", fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                    Text("• Marked for Review: $reviewCount", fontWeight = FontWeight.SemiBold, color = PurpleAccent)
                    Text("• Unattempted: $unattemptedCount", fontWeight = FontWeight.SemiBold, color = BreakingRed)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        viewModel.submitCurrentTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Confirm Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text("Continue Test")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    attempt: TestAttempt,
    test: MockTest,
    viewModel: ExamViewModel
) {
    BackHandler {
        viewModel.setTab(StudentTab.MOCK_TESTS)
    }

    var showSolutions by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance & Solutions", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setTab(StudentTab.MOCK_TESTS) }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = SurfaceWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Overall Score Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue800)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Total Score",
                            color = RoyalBlue100,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${attempt.score}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SurfaceWhite
                            )
                            Text(
                                text = " / ${attempt.maxMarks.toInt()}",
                                fontSize = 18.sp,
                                color = RoyalBlue100,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        val accuracy = if (attempt.correctCount + attempt.wrongCount > 0) {
                            (attempt.correctCount.toDouble() / (attempt.correctCount + attempt.wrongCount) * 100).toInt()
                        } else 0
                        val percentage = ((attempt.score / attempt.maxMarks) * 100).toInt()

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$percentage%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                                Text("Percentage", fontSize = 11.sp, color = RoyalBlue100)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$accuracy%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                                Text("Accuracy", fontSize = 11.sp, color = RoyalBlue100)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${attempt.timeSpentSeconds / 60}m ${attempt.timeSpentSeconds % 60}s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                                Text("Time Taken", fontSize = 11.sp, color = RoyalBlue100)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Rank #42", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentAmberLight)
                                Text("Est. Rank", fontSize = 11.sp, color = RoyalBlue100)
                            }
                        }
                    }
                }
            }

            // Stats Breakdown
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreenLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${attempt.correctCount}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            Text("Correct", fontSize = 11.sp, color = SuccessGreen)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = BreakingRedLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${attempt.wrongCount}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BreakingRed)
                            Text("Incorrect", fontSize = 11.sp, color = BreakingRed)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${attempt.unattemptedCount}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                            Text("Skipped", fontSize = 11.sp, color = TextSecondaryLight)
                        }
                    }
                }
            }

            // Review Answers Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review Answers & Rationales",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Question solutions
            items(test.questions) { q ->
                val userChoice = attempt.answersMap[q.questionNumber - 1]
                val isCorrect = userChoice == q.correctOptionIndex
                val isSkipped = userChoice == null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Question ${q.questionNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)

                            val statusBadge = when {
                                isCorrect -> "Correct (+${test.positiveMarks})" to SuccessGreen
                                isSkipped -> "Skipped (0.0)" to TextSecondaryLight
                                else -> "Incorrect (-${test.negativeMarks})" to BreakingRed
                            }

                            Text(statusBadge.first, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusBadge.second)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = q.questionText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        q.options.forEachIndexed { optIdx, optText ->
                            val isCorrectOpt = optIdx == q.correctOptionIndex
                            val isUserPicked = optIdx == userChoice

                            val bg = when {
                                isCorrectOpt -> SuccessGreenLight
                                isUserPicked && !isCorrectOpt -> BreakingRedLight
                                else -> SurfaceWhite
                            }

                            val border = when {
                                isCorrectOpt -> SuccessGreen
                                isUserPicked && !isCorrectOpt -> BreakingRed
                                else -> SurfaceBorderLight
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + optIdx)}. $optText",
                                    fontSize = 13.sp,
                                    fontWeight = if (isCorrectOpt || isUserPicked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = TextPrimaryLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = RoyalBlue50),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Explanation & Rationale:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue800
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.explanation,
                                    fontSize = 12.sp,
                                    color = TextPrimaryLight,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

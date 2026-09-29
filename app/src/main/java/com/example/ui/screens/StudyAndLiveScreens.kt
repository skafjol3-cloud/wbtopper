package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyMaterial
import com.example.data.repository.ExamDataProvider
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

@Composable
fun StudyMaterialsScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val materials by viewModel.studyMaterials.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL" to "All Resources",
        "Notes" to "Notes",
        "PDF" to "PDF",
        "Previous Year Questions" to "PYQs",
        "Short Notes" to "Short Notes",
        "Revision Notes" to "Revision Notes",
        "Practice Sets" to "Practice Sets"
    )

    val filteredMaterials = if (selectedCategory == "ALL") {
        materials
    } else {
        materials.filter { it.category == selectedCategory || it.fileType == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(bottom = 76.dp)
            .testTag("study_materials_screen")
    ) {
        // Material Category Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (catKey, catLabel) ->
                val isSelected = selectedCategory == catKey
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = catKey },
                    label = {
                        Text(
                            catLabel,
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

        if (filteredMaterials.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No study materials found in this category.", color = TextSecondaryLight, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMaterials) { mat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(Screen.PdfViewer(mat.id)) }
                            .testTag("material_item_${mat.id}"),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (mat.isFree) SuccessGreenLight else AccentAmberLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (mat.isFree) "FREE PDF" else "PREMIUM",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mat.isFree) SuccessGreen else AccentAmber,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = "Exam: ${mat.examCode.replace("_", " ")}",
                                    fontSize = 11.sp,
                                    color = TextMutedLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BreakingRedLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF",
                                        tint = BreakingRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mat.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight,
                                        lineHeight = 19.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${mat.subject} • ${mat.pageCount} Pages • ${mat.fileSize}",
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.showToast("Downloading '${mat.title}'...") },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download", fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { viewModel.navigateTo(Screen.PdfViewer(mat.id)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("View PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
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
fun PdfViewerScreen(
    materialId: String,
    viewModel: ExamViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val materials by viewModel.studyMaterials.collectAsState()
    val mat = materials.find { it.id == materialId } ?: materials.first()

    var currentPage by remember { mutableStateOf(1) }
    val totalPages = mat.pageCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mat.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showToast("Bookmark added for this document") }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark")
                    }
                    IconButton(onClick = { viewModel.showToast("Document saved to offline downloads") }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue50, contentColor = RoyalBlue800)
                    ) {
                        Text("Previous")
                    }

                    Text("Page $currentPage of $totalPages", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Button(
                        onClick = { if (currentPage < totalPages) currentPage++ },
                        enabled = currentPage < totalPages,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                    ) {
                        Text("Next Page")
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "${mat.subject.uppercase()} — HIGH-YIELD REVISION GUIDE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mat.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = SurfaceBorderLight)

                    Text(
                        text = "Document Page $currentPage / $totalPages\n\n" +
                                "1. ANATOMICAL & PHYSIOLOGICAL FOUNDATIONS:\n" +
                                "Standard anatomical references provide the baseline for clinical documentation and procedural interventions. In adult patient assessments, cardinal planes define directional relationships.\n\n" +
                                "2. CLINICAL RELEVANCE FOR NURSING OFFICERS:\n" +
                                "Recognizing hemodynamic stability requires continuous monitoring of arterial oxygenation, mean arterial pressure (MAP = [(2 x Diastolic) + Systolic] / 3), and pulse pressure parameters.\n\n" +
                                "3. HIGH YIELD EXAMINATION FACTS:\n" +
                                "• Normal adult core temperature: 36.5°C to 37.5°C (97.7°F to 99.5°F).\n" +
                                "• Optimal pulse pressure range: 30 to 50 mmHg.\n" +
                                "• Glasgow Coma Scale (GCS) maximum score: 15; Deep coma: 3.\n\n" +
                                "4. PUBLIC HEALTH FRAMEWORKS:\n" +
                                "National Health Mission guidelines mandate strict maternal mortality ratio (MMR) and infant mortality rate (IMR) reduction through institutional deliveries and early neonatal care.",
                        fontSize = 13.sp,
                        color = TextPrimaryLight,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    examCode: String,
    viewModel: ExamViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val mockTests by viewModel.mockTests.collectAsState()
    val examCategories by viewModel.examCategories.collectAsState()
    val entitlements by viewModel.studentEntitlements.collectAsState()
    val coupons by viewModel.coupons.collectAsState()

    val exam = examCategories.find { it.code == examCode }
        ?: ExamDataProvider.examCategories.find { it.code == examCode }
        ?: ExamDataProvider.examCategories.first()

    val relevantTests = mockTests.filter { it.examCode == examCode }.sortedBy { it.displayOrder }
    val isCategoryUnlocked = viewModel.isCategoryUnlocked(examCode)

    var showPurchaseModal by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var purchaseSuccess by remember { mutableStateOf(false) }

    val packagePrice = exam.packagePrice
    val discountPercent = if (appliedCoupon != null) {
        coupons.find { it.code.equals(appliedCoupon, true) }?.discountPercent ?: 0
    } else 0
    val finalPrice = if (discountPercent > 0) {
        packagePrice * (100 - discountPercent) / 100.0
    } else packagePrice

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exam.name, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isCategoryUnlocked) {
                        Surface(
                            color = SuccessGreenLight,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("UNLOCKED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
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
                .padding(16.dp)
        ) {
            // Exam Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue800)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${exam.name} (${exam.year})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                        if (exam.isNew) {
                            Surface(
                                color = BreakingRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("NEW", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SurfaceWhite, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = exam.description,
                        fontSize = 12.sp,
                        color = RoyalBlue100,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Candidates", fontSize = 11.sp, color = RoyalBlue100)
                            Text(exam.totalCandidates, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                        }
                        Column {
                            Text("Mock Tests", fontSize = 11.sp, color = RoyalBlue100)
                            Text("${relevantTests.size} Series", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                        }
                        Column {
                            Text("Category Price", fontSize = 11.sp, color = RoyalBlue100)
                            Text("₹${exam.packagePrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentAmberLight)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ₹199 ALL-ACCESS PACKAGE STATUS CARD
            if (!isCategoryUnlocked) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentAmber)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "WBTOPPER All-Access Package",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        text = "First mock test is FREE • Unlock ALL remaining tests",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${packagePrice.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF78350F))
                                Text("₹499", fontSize = 10.sp, color = Color(0xFF92400E), style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "One-time payment grants permanent access to all remaining full-length mock tests, clinical answer rationales, and state ranking.",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showPurchaseModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock All Mock Tests (₹${packagePrice.toInt()})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreenLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "All-Access Package Active",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = "Permanent unlocked access to all mock tests in this exam series.",
                                fontSize = 11.5.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Official Syllabus & Subject Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue50)
            ) {
                Text(
                    text = exam.syllabusSummary,
                    fontSize = 12.5.sp,
                    color = RoyalBlue800,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mock Test Series (${relevantTests.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Text(if (isCategoryUnlocked) "All Tests Unlocked" else "1 Free • ${relevantTests.size - 1} Locked", fontSize = 11.5.sp, color = TextSecondaryLight)
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (relevantTests.isEmpty()) {
                Text("New test series for this exam is releasing soon. Check back shortly.", fontSize = 13.sp, color = TextSecondaryLight)
            } else {
                relevantTests.forEachIndexed { index, test ->
                    val isFreeTest = test.isFree || index == 0
                    val isUnlocked = isFreeTest || isCategoryUnlocked

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (isUnlocked) {
                                    viewModel.navigateTo(Screen.TestInstructions(test.id))
                                } else {
                                    showPurchaseModal = true
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) SurfaceBorderLight else Color(0xFFFDE68A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isFreeTest) {
                                        Surface(
                                            color = SuccessGreenLight,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("FREE MOCK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    } else if (isUnlocked) {
                                        Surface(
                                            color = RoyalBlue50,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("UNLOCKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    } else {
                                        Surface(
                                            color = Color(0xFFFEF2F2),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = BreakingRed, modifier = Modifier.size(10.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("LOCKED • ₹${exam.packagePrice.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BreakingRed)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(test.subject, fontSize = 11.sp, color = TextSecondaryLight)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(test.title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${test.questions.size} Questions • ${test.durationMinutes} mins • +${test.positiveMarks}/-${test.negativeMarks} marking", fontSize = 11.sp, color = TextMutedLight)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            if (isUnlocked) {
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.TestInstructions(test.id)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isFreeTest) SuccessGreen else RoyalBlue800),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(if (isFreeTest) "Start Free" else "Attempt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { showPurchaseModal = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BreakingRed),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = BreakingRed, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Unlock", fontSize = 12.sp, color = BreakingRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // RAZORPAY ₹199 ALL-ACCESS PURCHASE MODAL
    // ==========================================
    if (showPurchaseModal) {
        AlertDialog(
            onDismissRequest = {
                if (!isProcessingPayment) showPurchaseModal = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = RoyalBlue800, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock ${exam.name}", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (purchaseSuccess) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Payment Verified!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("All mock tests in ${exam.name} are now permanently unlocked.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 13.sp, color = TextSecondaryLight)
                            }
                        }
                    } else {
                        Text(
                            text = "Get permanent access to all remaining full-length mock tests for ${exam.name} with one single payment.",
                            fontSize = 12.5.sp,
                            color = TextSecondaryLight
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Inclusions
                        Surface(
                            color = RoyalBlue50,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Package Inclusions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                Spacer(modifier = Modifier.height(4.dp))
                                exam.packageInclusions.forEach { inc ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(inc, fontSize = 11.5.sp, color = TextPrimaryLight)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Coupon Section
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it },
                                placeholder = { Text("Coupon Code (e.g. WELCOME30)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    val matched = coupons.find { it.code.equals(couponInput.trim(), true) && it.isActive }
                                    if (matched != null) {
                                        appliedCoupon = matched.code
                                    } else {
                                        appliedCoupon = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Apply", fontSize = 11.sp)
                            }
                        }

                        if (appliedCoupon != null) {
                            Text("Coupon '$appliedCoupon' applied! ($discountPercent% OFF)", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price summary
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Package Base Price:", fontSize = 12.sp, color = TextSecondaryLight)
                            Text("₹${packagePrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (discountPercent > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon Discount ($discountPercent%):", fontSize = 12.sp, color = SuccessGreen)
                                Text("-₹${(packagePrice - finalPrice).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Payable:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                            Text("₹${finalPrice.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = RoyalBlue800)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Secured by Razorpay. 100% verified SSL checkout.",
                            fontSize = 10.sp,
                            color = TextMutedLight,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (purchaseSuccess) {
                    Button(
                        onClick = {
                            showPurchaseModal = false
                            purchaseSuccess = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                    ) {
                        Text("Continue to Tests")
                    }
                } else {
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            viewModel.unlockCategoryPackage(
                                examCode = examCode,
                                basePrice = packagePrice,
                                couponCode = appliedCoupon
                            )
                            isProcessingPayment = false
                            purchaseSuccess = true
                        },
                        enabled = !isProcessingPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Pay ₹${finalPrice.toInt()} via Razorpay")
                        }
                    }
                }
            },
            dismissButton = {
                if (!purchaseSuccess) {
                    TextButton(onClick = { showPurchaseModal = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

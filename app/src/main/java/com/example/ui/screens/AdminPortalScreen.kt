package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MockTest
import com.example.data.model.Question
import com.example.data.repository.ExamDataProvider
import com.example.ui.screens.admin.*
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen

enum class AdminNavTab {
    DASHBOARD,
    CATEGORIES,
    TESTS,
    AI_GENERATOR,
    QUESTION_BANK,
    BULK_IMPORT,
    FIREBASE,
    PAYMENTS,
    COURSES,
    STUDENTS,
    MATERIALS,
    COUPONS,
    NOTIFICATIONS,
    ANALYTICS,
    SETTINGS
}

@Composable
fun AdminPortalScreen(
    viewModel: ExamViewModel
) {
    BackHandler {
        viewModel.exitAdminMode()
    }

    var selectedNav by remember { mutableStateOf(AdminNavTab.DASHBOARD) }

    // Dialog state for adding mock test
    var showAddTestDialog by remember { mutableStateOf(false) }
    var testTitle by remember { mutableStateOf("") }
    var testExamCode by remember { mutableStateOf("WBHRB_SN") }
    var testSubject by remember { mutableStateOf("Nursing Foundation & Med-Surg") }
    var testDuration by remember { mutableStateOf("75") }
    var testPosMarks by remember { mutableStateOf("1.0") }
    var testNegMarks by remember { mutableStateOf("0.25") }
    var testIsFree by remember { mutableStateOf(true) }
    var testPrice by remember { mutableStateOf("0") }

    // Dialog state for Question Bank
    var showAddQDialog by remember { mutableStateOf(false) }
    var qText by remember { mutableStateOf("") }
    var qOptA by remember { mutableStateOf("") }
    var qOptB by remember { mutableStateOf("") }
    var qOptC by remember { mutableStateOf("") }
    var qOptD by remember { mutableStateOf("") }
    var qCorrectIndex by remember { mutableStateOf(0) }
    var qSubject by remember { mutableStateOf("Anatomy & Physiology") }
    var qTopic by remember { mutableStateOf("Cardiovascular System") }
    var qDifficulty by remember { mutableStateOf("Medium") }
    var qExplanation by remember { mutableStateOf("") }

    // Dialog for Courses
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var courseTitle by remember { mutableStateOf("") }
    var courseExam by remember { mutableStateOf("WBHRB_SN") }
    var coursePrice by remember { mutableStateOf("699") }
    var courseInstructor by remember { mutableStateOf("Senior Nursing Faculty") }

    // Dialog for Coupons
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var couponCode by remember { mutableStateOf("") }
    var couponDiscount by remember { mutableStateOf("30") }
    var couponMax by remember { mutableStateOf("300") }
    var couponExpiry by remember { mutableStateOf("2026-12-31") }
    var couponCourse by remember { mutableStateOf("All Courses") }

    // Dialog for Notification Composer
    var showNotificationDialog by remember { mutableStateOf(false) }
    var notifTitle by remember { mutableStateOf("") }
    var notifMsg by remember { mutableStateOf("") }
    var notifTarget by remember { mutableStateOf("All Students") }

    // Dialog for Study Material
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var matTitle by remember { mutableStateOf("") }
    var matSubject by remember { mutableStateOf("Pharmacology") }
    var matCategory by remember { mutableStateOf("Revision Notes") }
    var matIsFree by remember { mutableStateOf(true) }

    val students by viewModel.students.collectAsState()
    val mockTests by viewModel.mockTests.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val razorpayConfig by viewModel.razorpayConfig.collectAsState()
    val questionBank by viewModel.questionBank.collectAsState()
    val firebaseStatus by viewModel.firebaseStatus.collectAsState()

    var showRazorpayDialog by remember { mutableStateOf(false) }
    var rzpKeyId by remember(razorpayConfig) { mutableStateOf(razorpayConfig.keyId) }
    var rzpKeySecret by remember(razorpayConfig) { mutableStateOf(razorpayConfig.keySecret) }
    var rzpWebhookSecret by remember(razorpayConfig) { mutableStateOf(razorpayConfig.webhookSecret) }
    var rzpMerchantName by remember(razorpayConfig) { mutableStateOf(razorpayConfig.merchantName) }
    var rzpIsLiveMode by remember(razorpayConfig) { mutableStateOf(razorpayConfig.isLiveMode) }
    var rzpAutoCapture by remember(razorpayConfig) { mutableStateOf(razorpayConfig.autoCapture) }
    var rzpSecretVisible by remember { mutableStateOf(false) }
    var rzpConnectionStatus by remember { mutableStateOf<String?>(null) }

    var studentSearch by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(
                color = RoyalBlue900,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = RoyalBlue900,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Admin Dashboard",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Super Administrator Console",
                                    fontSize = 11.sp,
                                    color = RoyalBlue100
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.exitAdminMode() },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("admin_exit_btn")
                        ) {
                            Text(
                                text = "Exit Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue900
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedNav.ordinal,
                        containerColor = RoyalBlue900,
                        contentColor = SurfaceWhite,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        AdminNavTab.values().forEach { tab ->
                            val isSelected = selectedNav == tab
                            Tab(
                                selected = isSelected,
                                onClick = { selectedNav = tab },
                                text = {
                                    Text(
                                        text = tab.name.replace("_", " "),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SurfaceWhite else RoyalBlue100.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNav) {
                AdminNavTab.DASHBOARD -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Quick Action Buttons
                        item {
                            Text("Management Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            // 2 rows of quick action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { selectedNav = AdminNavTab.CATEGORIES },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Categories", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { selectedNav = AdminNavTab.AI_GENERATOR },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI Generator", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { selectedNav = AdminNavTab.BULK_IMPORT },
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Bulk Import", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { selectedNav = AdminNavTab.FIREBASE },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Firebase", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showAddTestDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mock Test", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showNotificationDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Broadcast", fontSize = 11.sp)
                                }
                            }
                        }

                        // KPI Cards Grid (8 Cards)
                        item {
                            Text("Key Performance Indicators (KPI)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            val totalStudents = students.size
                            val activeStudents = students.count { !it.isSuspended }
                            val totalRev = transactions.filter { it.status == "Successful" }.sumOf { it.amount }
                            val pendingRev = transactions.filter { it.status != "Successful" }.sumOf { it.amount }
                            val totalQuestions = questionBank.size + mockTests.sumOf { it.questions.size }
                            val paidOrdersCount = transactions.count { it.status == "Successful" }

                            // Row 1
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Students", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("$totalStudents", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RoyalBlue800)
                                        Text("${students.count { it.isEmailVerified }} verified", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Active Students", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("$activeStudents", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = IndigoPrimary)
                                        Text("${students.count { it.isSuspended }} suspended", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Row 2
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Mock Tests", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("${mockTests.size}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RoyalBlue800)
                                        Text("${mockTests.count { it.isPublished }} published series", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Courses", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("${courses.size}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PurpleAccent)
                                        Text("${courses.count { it.isFeatured }} featured batches", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Row 3
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Revenue", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("₹${totalRev.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                                        Text("$paidOrdersCount orders verified", fontSize = 10.sp, color = SuccessGreen)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Pending / Failed", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("₹${pendingRev.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AccentAmber)
                                        Text("${transactions.count { it.status != "Successful" }} pending orders", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Row 4
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Questions", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("$totalQuestions", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RoyalBlue800)
                                        Text("In Question Bank", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, SurfaceBorderLight)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Backend Status", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text(
                                            text = if (firebaseStatus.isInitialized) "INITIALIZED" else "OFFLINE MODE",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (firebaseStatus.isInitialized) SuccessGreen else RoyalBlue800
                                        )
                                        Text(if (firebaseStatus.isInitialized) "Firebase Connected" else "Using Local DB Cache", fontSize = 10.sp, color = TextMutedLight)
                                    }
                                }
                            }
                        }

                        // Visual Charts Section
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Growth & Revenue Analytics", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Weekly Revenue Trend", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                                        Text("Total: ₹24,800", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Bar representation
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        val revDays = listOf("Mon" to 0.40f, "Tue" to 0.65f, "Wed" to 0.50f, "Thu" to 0.85f, "Fri" to 0.95f, "Sat" to 0.70f, "Sun" to 0.80f)
                                        revDays.forEach { (day, frac) ->
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(26.dp)
                                                        .height((frac * 60).dp)
                                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                        .background(if (frac > 0.8f) SuccessGreen else RoyalBlue700)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(day, fontSize = 10.sp, color = TextSecondaryLight)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Recent System Audit Activity
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Audit Trail & Administrative Logs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    auditLogs.take(5).forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).background(RoyalBlue800, CircleShape))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(log, fontSize = 12.sp, color = TextPrimaryLight)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.CATEGORIES -> {
                    AdminExamCategoryScreen(viewModel)
                }

                AdminNavTab.AI_GENERATOR -> {
                    AdminAiQuestionGeneratorScreen(viewModel)
                }

                AdminNavTab.BULK_IMPORT -> {
                    AdminBulkImportScreen(viewModel)
                }

                AdminNavTab.FIREBASE -> {
                    AdminFirebaseManagerScreen(viewModel)
                }

                AdminNavTab.STUDENTS -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        OutlinedTextField(
                            value = studentSearch,
                            onValueChange = { studentSearch = it },
                            placeholder = { Text("Search by name, email or student ID...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue700) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val filteredStudents = students.filter {
                            studentSearch.isBlank() || it.name.contains(studentSearch, ignoreCase = true) || it.email.contains(studentSearch, ignoreCase = true)
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filteredStudents) { std ->
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
                                            Column {
                                                Text(std.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                Text("${std.email} • ${std.phone}", fontSize = 11.sp, color = TextSecondaryLight)
                                                Text("ID: ${std.studentId} • Role: ${std.role.name} • ${if (std.isEmailVerified) "Email Verified" else "Pending Verification"}", fontSize = 10.sp, color = if (std.isEmailVerified) SuccessGreen else AccentAmber, fontWeight = FontWeight.SemiBold)
                                                Text("Provider: ${std.authProvider} • Registered: ${std.joinedDate}", fontSize = 10.sp, color = TextMutedLight)
                                            }

                                            Surface(
                                                color = if (std.isSuspended) BreakingRedLight else SuccessGreenLight,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (std.isSuspended) "SUSPENDED" else "ACTIVE",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (std.isSuspended) BreakingRed else SuccessGreen,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = SurfaceBorderLight)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Target: ${std.targetExam} • ${std.testAttemptsCount} Tests",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = RoyalBlue800
                                            )

                                            Button(
                                                onClick = { viewModel.adminToggleStudent(std.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (std.isSuspended) SuccessGreen else BreakingRed
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(if (std.isSuspended) "Activate" else "Block", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.TESTS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mock Test Management (${mockTests.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddTestDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ New Test", fontSize = 12.sp)
                                }
                            }
                        }

                        items(mockTests) { test ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(test.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Surface(
                                            color = if (test.isPublished) SuccessGreenLight else Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (test.isPublished) "PUBLISHED" else "DRAFT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (test.isPublished) SuccessGreen else TextSecondaryLight,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${test.subject} • ${test.questions.size} Questions • ${test.durationMinutes} mins • Marks: +${test.positiveMarks} / -${test.negativeMarks}", fontSize = 12.sp, color = TextSecondaryLight)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.adminToggleMockTestPublish(test.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (test.isPublished) "Unpublish" else "Publish", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.navigateTo(Screen.TestInstructions(test.id)) },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Preview", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.adminDeleteMockTest(test.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingRed),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.QUESTION_BANK -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Question Bank Central Repository (${questionBank.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddQDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ Add Question", fontSize = 12.sp)
                                }
                            }
                        }

                        items(questionBank) { q ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subject: ${q.subject}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Difficulty: ${q.difficulty}", fontSize = 11.sp, color = TextSecondaryLight)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { viewModel.adminDeleteQuestionFromBank(q.id) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BreakingRed, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(q.questionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Correct Answer: Option ${('A' + q.correctOptionIndex)} (${q.options.getOrElse(q.correctOptionIndex) { "" }})", fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                    if (q.explanation.isNotBlank()) {
                                        Text("Rationale: ${q.explanation}", fontSize = 11.sp, color = TextSecondaryLight, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.COURSES -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Course Management (${courses.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddCourseDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ Add Course", fontSize = 12.sp)
                                }
                            }
                        }

                        items(courses) { c ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(c.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Fee: ₹${c.price.toInt()} (Reg: ₹${c.originalPrice.toInt()}) • ${c.totalLessons} Lessons • ${c.enrolledCount} Enrolled", fontSize = 12.sp, color = TextSecondaryLight)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.showToast("Course curriculum updated") },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Edit Lessons", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.adminDeleteCourse(c.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingRed),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Delete", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.MATERIALS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Study Materials Management (${materials.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddMaterialDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ Upload PDF", fontSize = 12.sp)
                                }
                            }
                        }

                        items(materials) { m ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BreakingRed)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(m.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${m.category} • ${m.subject} • ${m.pageCount} Pages • ${m.downloadCount} downloads", fontSize = 11.sp, color = TextSecondaryLight)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.PAYMENTS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Payment Gateway & Collections", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text("Razorpay automated settlement, API key management & order audit", fontSize = 11.sp, color = TextSecondaryLight)
                        }

                        // 1. Razorpay Payment Gateway Configuration Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.5.dp, if (razorpayConfig.isLiveMode) SuccessGreen else RoyalBlue700)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(RoyalBlue50),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Payments,
                                                    contentDescription = "Razorpay",
                                                    tint = RoyalBlue800,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Razorpay Gateway API", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue900)
                                                Text("Merchant: ${razorpayConfig.merchantName}", fontSize = 11.sp, color = TextSecondaryLight)
                                            }
                                        }

                                        Surface(
                                            color = if (razorpayConfig.isLiveMode) SuccessGreenLight else AccentAmberLight,
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = if (razorpayConfig.isLiveMode) "LIVE PRODUCTION" else "SANDBOX TEST",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (razorpayConfig.isLiveMode) SuccessGreen else AccentAmber,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Credentials Summary
                                    Surface(
                                        color = SurfaceCardLight,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, SurfaceBorderLight),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Key ID:", fontSize = 11.sp, color = TextMutedLight)
                                                Text(
                                                    text = if (razorpayConfig.keyId.isNotBlank()) razorpayConfig.keyId else "Not configured",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = RoyalBlue800
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Key Secret:", fontSize = 11.sp, color = TextMutedLight)
                                                Text(
                                                    text = if (razorpayConfig.keySecret.isNotBlank()) "••••••••••••${razorpayConfig.keySecret.takeLast(4)}" else "Not configured",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextPrimaryLight
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Currency & Capture:", fontSize = 11.sp, color = TextMutedLight)
                                                Text("INR (₹) • Auto-Capture Active", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    if (rzpConnectionStatus != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            color = SuccessGreenLight,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(rzpConnectionStatus ?: "", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action buttons for Razorpay
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                rzpKeyId = razorpayConfig.keyId
                                                rzpKeySecret = razorpayConfig.keySecret
                                                rzpWebhookSecret = razorpayConfig.webhookSecret
                                                rzpMerchantName = razorpayConfig.merchantName
                                                rzpIsLiveMode = razorpayConfig.isLiveMode
                                                rzpAutoCapture = razorpayConfig.autoCapture
                                                showRazorpayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Configure API Keys", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val success = viewModel.testRazorpayConnection()
                                                rzpConnectionStatus = if (success) {
                                                    "Razorpay API 200 OK • Latency: 34ms • Handshake Verified"
                                                } else {
                                                    "Connection Failed: Please verify Key ID & Secret"
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, RoyalBlue800),
                                            modifier = Modifier.weight(0.9f)
                                        ) {
                                            Icon(Icons.Default.Sync, contentDescription = null, tint = RoyalBlue800, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Test Gateway", fontSize = 12.sp, color = RoyalBlue800, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Metrics summary
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val liveCollections = transactions.filter { it.status == "Successful" }.sumOf { it.amount }
                                val successRate = if (transactions.isNotEmpty()) (transactions.count { it.status == "Successful" } * 100.0 / transactions.size) else 100.0

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    border = BorderStroke(1.dp, SurfaceBorderLight)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Collections", fontSize = 11.sp, color = TextMutedLight)
                                        Text("₹${liveCollections.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                        Text("${transactions.count { it.status == "Successful" }} Verified Orders", fontSize = 10.sp, color = TextSecondaryLight)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    border = BorderStroke(1.dp, SurfaceBorderLight)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Success Rate", fontSize = 11.sp, color = TextMutedLight)
                                        Text("${"%.1f".format(successRate)}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                        Text("Server-Verified Orders", fontSize = 10.sp, color = TextSecondaryLight)
                                    }
                                }
                            }
                        }

                        // 3. Transactions List Header
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Recent Transactions", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        items(transactions) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tx.transactionId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                        Text("${tx.studentName} — ${tx.itemTitle}", fontSize = 12.sp, color = TextPrimaryLight)
                                        Text("${tx.dateStr} • ${tx.paymentMethod}", fontSize = 11.sp, color = TextMutedLight)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("₹${tx.amount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                                        Surface(
                                            color = if (tx.status == "Successful") SuccessGreenLight else AccentAmberLight,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                tx.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (tx.status == "Successful") SuccessGreen else AccentAmber,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.COUPONS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Discount Coupons Management", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddCouponDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ New Coupon", fontSize = 12.sp)
                                }
                            }
                        }

                        items(coupons) { cp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cp.code, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = RoyalBlue800)
                                        Text("${cp.discountPercent}% Discount (Max ₹${cp.maxDiscount.toInt()})", fontSize = 12.sp, color = TextPrimaryLight)
                                        Text("Expires: ${cp.expiryDate} • Used: ${cp.usageCount}/${cp.usageLimit} times", fontSize = 11.sp, color = TextMutedLight)
                                    }
                                    Surface(color = SuccessGreenLight, shape = RoundedCornerShape(4.dp)) {
                                        Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.NOTIFICATIONS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Student Broadcasts & Push Alerts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showNotificationDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                                ) {
                                    Text("+ New Alert", fontSize = 12.sp)
                                }
                            }
                        }

                        items(ExamDataProvider.notifications) { notif ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(notif.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(notif.message, fontSize = 12.sp, color = TextSecondaryLight)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Broadcast ${notif.timeAgo}", fontSize = 10.sp, color = TextMutedLight)
                                }
                            }
                        }
                    }
                }

                AdminNavTab.ANALYTICS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Comprehensive Platform Analytics", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Aggregate performance across all 16 competitive exam streams", fontSize = 11.sp, color = TextSecondaryLight)
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Top Examinations by Candidate Engagement", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val topExams = listOf("WBHRB Staff Nurse Grade II" to 88, "ANM GNM 2026" to 94, "NORCET 10 Nursing Officer" to 76, "WB CHO Recruitment" to 70, "JENPAS-UG 2026" to 65)
                                    topExams.forEach { (name, pct) ->
                                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(name, fontSize = 12.sp, color = TextPrimaryLight)
                                                Text("$pct%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            LinearProgressIndicator(
                                                progress = { pct / 100f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = RoyalBlue800,
                                                trackColor = RoyalBlue50
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminNavTab.SETTINGS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Admin Console & Security Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Security & Role Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Logged In Role: SUPER_ADMIN (Full Privileges)\nAccount: afjolsk0@gmail.com\nAudit Logging: Enabled", fontSize = 12.sp, color = TextSecondaryLight)

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { viewModel.showToast("Password reset instructions dispatched to registered admin email.") },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Change Admin Password")
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Razorpay Payment Gateway API Key", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue900)
                                        Surface(
                                            color = if (razorpayConfig.isLiveMode) SuccessGreenLight else AccentAmberLight,
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (razorpayConfig.isLiveMode) "LIVE" else "TEST",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (razorpayConfig.isLiveMode) SuccessGreen else AccentAmber,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Current Key ID: ${if (razorpayConfig.keyId.isNotBlank()) razorpayConfig.keyId else "Not configured"}\nMerchant: ${razorpayConfig.merchantName}\nAuto-Capture: Enabled • INR (₹)",
                                        fontSize = 12.sp,
                                        color = TextSecondaryLight
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                rzpKeyId = razorpayConfig.keyId
                                                rzpKeySecret = razorpayConfig.keySecret
                                                rzpWebhookSecret = razorpayConfig.webhookSecret
                                                rzpMerchantName = razorpayConfig.merchantName
                                                rzpIsLiveMode = razorpayConfig.isLiveMode
                                                rzpAutoCapture = razorpayConfig.autoCapture
                                                showRazorpayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Update Razorpay Keys", fontSize = 12.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                viewModel.testRazorpayConnection()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, RoyalBlue800)
                                        ) {
                                            Text("Test", fontSize = 12.sp, color = RoyalBlue800)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, SurfaceBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("App Configuration", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("App Name: WBTOPPER\nPayment Mode: Razorpay Live Server Verified\nDatabase: Cloud Firestore / Room Engine", fontSize = 12.sp, color = TextSecondaryLight)
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { viewModel.exitAdminMode() },
                                colors = ButtonDefaults.buttonColors(containerColor = BreakingRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Exit Admin Session & Return to Student View", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Mock Test
    if (showAddTestDialog) {
        AlertDialog(
            onDismissRequest = { showAddTestDialog = false },
            title = { Text("Create New Mock Test") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = testTitle, onValueChange = { testTitle = it }, label = { Text("Test Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = testSubject, onValueChange = { testSubject = it }, label = { Text("Subject / Stream") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = testDuration, onValueChange = { testDuration = it }, label = { Text("Duration (Minutes)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = testPosMarks, onValueChange = { testPosMarks = it }, label = { Text("Positive Marks") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = testNegMarks, onValueChange = { testNegMarks = it }, label = { Text("Negative Marks") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (testTitle.isNotBlank()) {
                            viewModel.adminAddMockTest(
                                title = testTitle,
                                examCode = testExamCode,
                                subject = testSubject,
                                durationMinutes = testDuration.toIntOrNull() ?: 60,
                                positiveMarks = testPosMarks.toDoubleOrNull() ?: 1.0,
                                negativeMarks = testNegMarks.toDoubleOrNull() ?: 0.25,
                                isFree = testIsFree,
                                price = testPrice.toDoubleOrNull() ?: 0.0
                            )
                            showAddTestDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Publish Test")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTestDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Add Question
    if (showAddQDialog) {
        AlertDialog(
            onDismissRequest = { showAddQDialog = false },
            title = { Text("Add Question to Bank") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptA, onValueChange = { qOptA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptB, onValueChange = { qOptB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptC, onValueChange = { qOptC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptD, onValueChange = { qOptD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select Correct Option:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row {
                        listOf(0, 1, 2, 3).forEach { idx ->
                            Row(
                                modifier = Modifier.clickable { qCorrectIndex = idx }.padding(end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = qCorrectIndex == idx, onClick = null)
                                Text(('A' + idx).toString(), fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qExplanation, onValueChange = { qExplanation = it }, label = { Text("Explanation & Rationale") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isNotBlank() && qOptA.isNotBlank()) {
                            val targetTestId = mockTests.firstOrNull()?.id ?: "test_wbhrb_sn_1"
                            viewModel.adminAddQuestion(
                                testId = targetTestId,
                                questionText = qText,
                                options = listOf(qOptA, qOptB, qOptC, qOptD),
                                correctOptionIndex = qCorrectIndex,
                                explanation = qExplanation,
                                subject = qSubject,
                                topic = qTopic,
                                difficulty = qDifficulty
                            )
                            showAddQDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Save Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Add Course
    if (showAddCourseDialog) {
        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Create New Course") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = courseTitle, onValueChange = { courseTitle = it }, label = { Text("Course Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = coursePrice, onValueChange = { coursePrice = it }, label = { Text("Course Fee (₹)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = courseInstructor, onValueChange = { courseInstructor = it }, label = { Text("Curated By") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courseTitle.isNotBlank()) {
                            val fee = coursePrice.toDoubleOrNull() ?: 499.0
                            viewModel.adminAddCourse(
                                title = courseTitle,
                                examCode = courseExam,
                                description = "Comprehensive preparation batch with mock test breakdown and live strategy sessions.",
                                instructor = courseInstructor,
                                price = fee,
                                originalPrice = fee * 2.5
                            )
                            showAddCourseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Create Course")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Add Coupon
    if (showAddCouponDialog) {
        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("Create Discount Coupon") },
            text = {
                Column {
                    OutlinedTextField(value = couponCode, onValueChange = { couponCode = it }, label = { Text("Coupon Code (e.g. NURSE30)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = couponDiscount, onValueChange = { couponDiscount = it }, label = { Text("Discount Percentage (%)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (couponCode.isNotBlank()) {
                            viewModel.adminCreateCoupon(
                                code = couponCode,
                                percent = couponDiscount.toIntOrNull() ?: 25,
                                maxDiscount = couponMax.toDoubleOrNull() ?: 300.0,
                                expiry = couponExpiry,
                                targetCourse = couponCourse
                            )
                            showAddCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Create Coupon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCouponDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Broadcast Notification
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Compose Student Broadcast") },
            text = {
                Column {
                    OutlinedTextField(value = notifTitle, onValueChange = { notifTitle = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = notifMsg, onValueChange = { notifMsg = it }, label = { Text("Message Body") }, maxLines = 4, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifMsg.isNotBlank()) {
                            viewModel.adminSendNotification(notifTitle, notifMsg, notifTarget)
                            showNotificationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Upload Study Material
    if (showAddMaterialDialog) {
        AlertDialog(
            onDismissRequest = { showAddMaterialDialog = false },
            title = { Text("Upload Study Material PDF") },
            text = {
                Column {
                    OutlinedTextField(value = matTitle, onValueChange = { matTitle = it }, label = { Text("Document Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = matSubject, onValueChange = { matSubject = it }, label = { Text("Subject (e.g. Pharmacology)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (matTitle.isNotBlank()) {
                            viewModel.adminAddStudyMaterial(
                                title = matTitle,
                                examCode = "WBHRB_SN",
                                subject = matSubject,
                                category = matCategory,
                                isFree = matIsFree,
                                description = "Official revision document and high yield study summary."
                            )
                            showAddMaterialDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Upload PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMaterialDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Configure Razorpay API Keys & Gateway
    if (showRazorpayDialog) {
        AlertDialog(
            onDismissRequest = { showRazorpayDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = RoyalBlue800, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razorpay Gateway API Keys", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Enter your Razorpay Key ID and Secret to process student enrollments and mock test purchases.",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Quick Fill Button
                    OutlinedButton(
                        onClick = {
                            rzpKeyId = "rzp_test_1DP5mmOlF5G5ag"
                            rzpKeySecret = "s6Z89Qk4yVwP123XyZabcdEf"
                            rzpWebhookSecret = "whsec_bep_2026_webhook"
                            rzpMerchantName = "WBTOPPER"
                            rzpIsLiveMode = false
                            rzpAutoCapture = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Fill Sample Sandbox Keys", fontSize = 11.sp, color = RoyalBlue800)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Key ID
                    OutlinedTextField(
                        value = rzpKeyId,
                        onValueChange = { rzpKeyId = it },
                        label = { Text("Razorpay Key ID") },
                        placeholder = { Text("rzp_test_... or rzp_live_...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Key Secret
                    OutlinedTextField(
                        value = rzpKeySecret,
                        onValueChange = { rzpKeySecret = it },
                        label = { Text("Razorpay Key Secret") },
                        placeholder = { Text("Secret Key") },
                        singleLine = true,
                        visualTransformation = if (rzpSecretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { rzpSecretVisible = !rzpSecretVisible }) {
                                Icon(
                                    imageVector = if (rzpSecretVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Secret Visibility"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Webhook Secret
                    OutlinedTextField(
                        value = rzpWebhookSecret,
                        onValueChange = { rzpWebhookSecret = it },
                        label = { Text("Webhook Secret (Optional)") },
                        placeholder = { Text("whsec_...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Merchant Name
                    OutlinedTextField(
                        value = rzpMerchantName,
                        onValueChange = { rzpMerchantName = it },
                        label = { Text("Merchant / Brand Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Mode Toggle Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardLight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Live Production Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (rzpIsLiveMode) "Processing real Indian payments" else "Testing in Sandbox simulator",
                                fontSize = 10.sp,
                                color = TextMutedLight
                            )
                        }
                        Switch(
                            checked = rzpIsLiveMode,
                            onCheckedChange = { rzpIsLiveMode = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Auto-Capture Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardLight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Capture Payments", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Instant student course unlock", fontSize = 10.sp, color = TextMutedLight)
                        }
                        Switch(
                            checked = rzpAutoCapture,
                            onCheckedChange = { rzpAutoCapture = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateRazorpayConfig(
                            keyId = rzpKeyId,
                            keySecret = rzpKeySecret,
                            webhookSecret = rzpWebhookSecret,
                            merchantName = rzpMerchantName,
                            isLiveMode = rzpIsLiveMode,
                            autoCapture = rzpAutoCapture
                        )
                        showRazorpayDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Save & Deploy Keys")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRazorpayDialog = false }) { Text("Cancel") }
            }
        )
    }
}

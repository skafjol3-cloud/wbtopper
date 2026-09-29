package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceConnectionState
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel

enum class FirebaseModuleTab {
    OVERVIEW,
    AUTH,
    FIRESTORE,
    STORAGE,
    FUNCTIONS,
    RULES,
    SETUP_WIZARD
}

@Composable
fun AdminFirebaseManagerScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val firebaseStatus by viewModel.firebaseStatus.collectAsState()
    val categories by viewModel.examCategories.collectAsState()
    val tests by viewModel.mockTests.collectAsState()
    val questions by viewModel.questionBank.collectAsState()
    val students by viewModel.students.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var activeTab by remember { mutableStateOf(FirebaseModuleTab.OVERVIEW) }
    var isVerifying by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header with verified live status badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Firebase Live Console",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800
                )
                Text(
                    text = "Project: ${firebaseStatus.projectId} • Region: ${firebaseStatus.region}",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }

            // Real status indicator badge (Never claimed Deployed unless verified)
            Surface(
                color = when {
                    firebaseStatus.isInitialized && firebaseStatus.isFirestoreConnected -> SuccessGreenLight
                    firebaseStatus.isInitialized -> AccentAmberLight
                    else -> Color(0xFFF1F5F9)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    firebaseStatus.isInitialized && firebaseStatus.isFirestoreConnected -> SuccessGreen
                                    firebaseStatus.isInitialized -> AccentAmber
                                    else -> Color.Gray
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            firebaseStatus.isInitialized && firebaseStatus.isFirestoreConnected -> "CONNECTED"
                            firebaseStatus.isInitialized -> "INITIALIZED"
                            else -> "NOT CONFIGURED"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            firebaseStatus.isInitialized && firebaseStatus.isFirestoreConnected -> SuccessGreen
                            firebaseStatus.isInitialized -> AccentAmber
                            else -> Color.DarkGray
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Refresh & Verify Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Last verified: ${firebaseStatus.lastSyncTime}",
                fontSize = 11.sp,
                color = TextMutedLight
            )
            OutlinedButton(
                onClick = {
                    isVerifying = true
                    viewModel.refreshFirebaseConnection()
                    viewModel.showToast("Service health check initiated...")
                    isVerifying = false
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Verify Services Now", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Module Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab.ordinal,
            edgePadding = 0.dp,
            containerColor = SurfaceWhite
        ) {
            FirebaseModuleTab.values().forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = {
                        Text(
                            text = tab.name.replace("_", " ").capitalizeWords(),
                            fontSize = 11.5.sp,
                            fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            FirebaseModuleTab.OVERVIEW -> {
                // Connection Health Cards (Verified Service Statuses)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RealServiceHealthCard(
                        title = "Authentication",
                        state = firebaseStatus.authStatus,
                        detail = "${students.size} Student Accounts",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    RealServiceHealthCard(
                        title = "Cloud Firestore",
                        state = firebaseStatus.firestoreStatus,
                        detail = "${categories.size} Categories • ${tests.size} Tests",
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RealServiceHealthCard(
                        title = "Cloud Storage",
                        state = firebaseStatus.storageStatus,
                        detail = "${firebaseStatus.storageUsedMb} MB • ${firebaseStatus.storageFiles.size} PDFs",
                        icon = Icons.Default.Folder,
                        modifier = Modifier.weight(1f)
                    )
                    RealServiceHealthCard(
                        title = "Cloud Functions",
                        state = firebaseStatus.functionsStatus,
                        detail = if (firebaseStatus.functionsStatus == ServiceConnectionState.DEPLOYED) "v${firebaseStatus.functionsVersion} Verified" else "Not Deployed",
                        icon = Icons.Default.Functions,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Database Live Metrics Summary (Real DB queries)
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
                            Text("Database Metrics (Live Queries)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                            Surface(
                                color = if (firebaseStatus.isLiveFirestoreActive) SuccessGreenLight else RoyalBlue50,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (firebaseStatus.isLiveFirestoreActive) "LIVE FIRESTORE" else "ROOM SQLITE CACHE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (firebaseStatus.isLiveFirestoreActive) SuccessGreen else RoyalBlue800,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MetricRow("Exam Categories (exam_categories):", "${categories.size} streams active")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceBorderLight)

                        MetricRow("Mock Tests (mock_tests):", "${tests.size} mock test series")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceBorderLight)

                        MetricRow("Question Bank (questions):", "${questions.size + tests.sumOf { it.questions.size }} MCQs indexed")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceBorderLight)

                        MetricRow("Registered Students (users):", "${students.size} registered accounts")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceBorderLight)

                        MetricRow("Payment Orders (orders):", "${transactions.size} transaction records")
                    }
                }
            }

            FirebaseModuleTab.AUTH -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Firebase Authentication & Registered Students", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(firebaseStatus.authStatusMessage, fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Registered Student Accounts (${students.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        students.forEach { std ->
                            Surface(
                                color = SurfaceCardLight,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SurfaceBorderLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(std.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${std.email} • ${std.phone}", fontSize = 11.sp, color = TextSecondaryLight)
                                        Text("ID: ${std.studentId} • Role: ${std.role.name} • ${std.authProvider}", fontSize = 10.sp, color = TextMutedLight)
                                    }

                                    Surface(
                                        color = if (std.isEmailVerified) SuccessGreenLight else AccentAmberLight,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (std.isEmailVerified) "VERIFIED" else "PENDING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (std.isEmailVerified) SuccessGreen else AccentAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FirebaseModuleTab.FIRESTORE -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cloud Firestore Collections", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(firebaseStatus.firestoreStatusMessage, fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Managed Collections in Production:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        listOf(
                            Triple("exam_categories", "${categories.size} documents", "Dynamic syllabus, ₹199 price, free test count"),
                            Triple("mock_tests", "${tests.size} documents", "Questions, duration, marking scheme, publication status"),
                            Triple("questions", "${questions.size} documents", "Central question bank with rationales & difficulty"),
                            Triple("users", "${students.size} documents", "Student profiles, target exams, account status"),
                            Triple("orders", "${transactions.size} documents", "Razorpay verified transactions & payment IDs"),
                            Triple("entitlements", "${transactions.size} documents", "Category package unlocks granted by server functions")
                        ).forEach { (col, count, desc) ->
                            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(col, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = RoyalBlue800)
                                    Text(count, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                                }
                                Text(desc, fontSize = 11.sp, color = TextSecondaryLight)
                            }
                        }
                    }
                }
            }

            FirebaseModuleTab.STORAGE -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Firebase Cloud Storage (Buckets)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(firebaseStatus.storageStatusMessage, fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Storage Bucket Usage:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${firebaseStatus.storageUsedMb} MB used", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Managed Study Material Files:", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        if (firebaseStatus.storageFiles.isEmpty()) {
                            Text(
                                "No remote files listed yet (bucket empty or awaiting google-services.json).",
                                fontSize = 11.5.sp,
                                color = TextMutedLight,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            firebaseStatus.storageFiles.forEach { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BreakingRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(file.name, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Text("${(file.sizeBytes / 1024)} KB", fontSize = 11.sp, color = TextMutedLight)
                                }
                            }
                        }
                    }
                }
            }

            FirebaseModuleTab.FUNCTIONS -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cloud Functions Deployment Status", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(firebaseStatus.functionsStatusMessage, fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actual function deployment check
                        FunctionItemRow(
                            name = "checkSystemHealth",
                            desc = "Deployment verification & server latency check",
                            status = if (firebaseStatus.functionsStatus == ServiceConnectionState.DEPLOYED) "DEPLOYED" else "NOT DEPLOYED"
                        )
                        FunctionItemRow(
                            name = "createRazorpayOrder",
                            desc = "Server-side order creation with Razorpay SDK",
                            status = if (firebaseStatus.functionsStatus == ServiceConnectionState.DEPLOYED) "DEPLOYED" else "NOT DEPLOYED"
                        )
                        FunctionItemRow(
                            name = "verifyRazorpayPayment",
                            desc = "HMAC-SHA256 signature verification & entitlement grant",
                            status = if (firebaseStatus.functionsStatus == ServiceConnectionState.DEPLOYED) "DEPLOYED" else "NOT DEPLOYED"
                        )
                        FunctionItemRow(
                            name = "razorpayWebhook",
                            desc = "Server-to-server webhook receiver with signature validation",
                            status = if (firebaseStatus.functionsStatus == ServiceConnectionState.DEPLOYED) "DEPLOYED" else "NOT DEPLOYED"
                        )
                    }
                }
            }

            FirebaseModuleTab.RULES -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Production Security Rules", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Configured in firebase/firestore.rules and firebase/storage.rules.", fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = RoyalBlue50,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = """
// Firestore Security Rules (Strict Role-Based)
match /exam_categories/{id} {
  allow read: if true;
  allow write: if request.auth.token.role == 'admin' || request.auth.token.admin == true;
}
match /mock_tests/{id} {
  allow read: if resource.data.isPublished == true || request.auth.token.role == 'admin';
  allow write: if request.auth.token.role == 'admin';
}
match /entitlements/{id} {
  allow read: if request.auth.token.email == resource.data.studentEmail;
  allow write: if false; // Cloud Functions only!
}
                                """.trimIndent(),
                                fontSize = 11.sp,
                                color = RoyalBlue900,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            FirebaseModuleTab.SETUP_WIZARD -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Firebase Setup Wizard for Beginners", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Text("Follow these numbered steps to connect your Firebase project:", fontSize = 12.sp, color = TextSecondaryLight)

                        Spacer(modifier = Modifier.height(12.dp))

                        StepItem("1", "Create Firebase Project", "Go to console.firebase.google.com and click 'Add Project'. Name it 'WBTOPPER'.")
                        StepItem("2", "Add Android App & Download Config", "Register package 'com.aistudio.wbtopper'. Download 'google-services.json' and place it in the app's root folder `/app/google-services.json`.")
                        StepItem("3", "Enable Authentication", "In Firebase Console > Authentication > Sign-in method, enable 'Email/Password' and 'Google'.")
                        StepItem("4", "Create Cloud Firestore", "In Firebase Console > Firestore Database, click 'Create database'. Choose region 'asia-south1 (Mumbai)' and deploy the provided firestore.rules.")
                        StepItem("5", "Enable Firebase Storage", "In Firebase Console > Storage, click 'Get Started'. Deploy the provided storage.rules.")
                        StepItem("6", "Deploy Cloud Functions", "In terminal: run 'cd firebase/functions && npm install && firebase deploy --only functions'.")
                        StepItem("7", "Set Razorpay Secrets", "Run: 'firebase functions:config:set razorpay.key_id=\"YOUR_KEY\" razorpay.key_secret=\"YOUR_SECRET\"'.")
                    }
                }
            }
        }
    }
}

@Composable
fun RealServiceHealthCard(
    title: String,
    state: ServiceConnectionState,
    detail: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val (statusLabel, statusColor, bgColor) = when (state) {
        ServiceConnectionState.CONNECTED -> Triple("CONNECTED", SuccessGreen, SuccessGreenLight)
        ServiceConnectionState.DEPLOYED -> Triple("DEPLOYED", SuccessGreen, SuccessGreenLight)
        ServiceConnectionState.NOT_CONFIGURED -> Triple("NOT CONFIGURED", Color(0xFF64748B), Color(0xFFF1F5F9))
        ServiceConnectionState.NOT_DEPLOYED -> Triple("NOT DEPLOYED", Color(0xFFD97706), Color(0xFFFEF3C7))
        ServiceConnectionState.UNVERIFIED -> Triple("UNVERIFIED", Color(0xFFD97706), Color(0xFFFEF3C7))
        ServiceConnectionState.ERROR -> Triple("ERROR", BreakingRed, BreakingRedLight)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, SurfaceBorderLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = RoyalBlue800, modifier = Modifier.size(18.dp))
                Surface(color = bgColor, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = statusLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
            Text(detail, fontSize = 11.sp, color = TextSecondaryLight)
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondaryLight)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
    }
}

@Composable
fun FunctionItemRow(name: String, desc: String, status: String) {
    Surface(
        color = SurfaceCardLight,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SurfaceBorderLight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                Text(desc, fontSize = 11.sp, color = TextSecondaryLight)
            }

            Surface(
                color = if (status == "DEPLOYED") SuccessGreenLight else Color(0xFFFEF3C7),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (status == "DEPLOYED") SuccessGreen else Color(0xFFD97706),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun StepItem(stepNum: String, title: String, instruction: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(RoyalBlue800),
            contentAlignment = Alignment.Center
        ) {
            Text(stepNum, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
            Text(instruction, fontSize = 11.5.sp, color = TextSecondaryLight)
        }
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

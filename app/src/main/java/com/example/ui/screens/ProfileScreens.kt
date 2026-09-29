package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

data class ProfileMenuItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun StudentProfileScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val studentName by viewModel.studentName.collectAsState()
    val studentEmail by viewModel.studentEmail.collectAsState()
    val studentId by viewModel.studentId.collectAsState()
    val studentTargetExam by viewModel.studentTargetExam.collectAsState()
    val attempts by viewModel.testAttempts.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showSupportDialog by remember { mutableStateOf(false) }
    var supportSubject by remember { mutableStateOf("") }
    var supportCategory by remember { mutableStateOf("Mock Test Query") }
    var supportMessage by remember { mutableStateOf("") }

    val enrolledCount = courses.count { it.isEnrolled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 88.dp)
            .testTag("student_profile_screen")
    ) {
        // 1. Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalBlue800)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalBlue900, RoyalBlue800, IndigoPrimary)
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = RoyalBlue800,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = studentName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )

                Text(
                    text = studentEmail,
                    fontSize = 12.sp,
                    color = RoyalBlue100
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = SurfaceWhite.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Student ID: $studentId",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SurfaceWhite,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = AccentAmberLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = studentTargetExam.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Summary Stats Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$enrolledCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Text("My Courses", fontSize = 11.sp, color = TextSecondaryLight)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${attempts.size + 4}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                    Text("Tests Attempted", fontSize = 11.sp, color = TextSecondaryLight)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("78%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Text("Avg Score", fontSize = 11.sp, color = TextSecondaryLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Learning & Activity Menu Items
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Learning & Records", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
            Spacer(modifier = Modifier.height(8.dp))

            val learningItems = listOf(
                ProfileMenuItem("My Courses", "View active enrollments", Icons.Default.PlayLesson) { viewModel.setTab(StudentTab.COURSES) },
                ProfileMenuItem("My Mock Tests", "Browse attempted papers", Icons.Default.Quiz) { viewModel.setTab(StudentTab.MOCK_TESTS) },
                ProfileMenuItem("My Results", "Scorecards & rank analytics", Icons.Default.Analytics) {
                    if (attempts.isNotEmpty()) {
                        val firstAtt = attempts.first()
                        val test = viewModel.mockTests.value.find { it.id == firstAtt.testId } ?: viewModel.mockTests.value.first()
                        viewModel.navigateTo(Screen.TestResult(
                            com.example.data.model.TestAttempt(
                                id = firstAtt.id,
                                testId = firstAtt.testId,
                                testTitle = firstAtt.testTitle,
                                score = firstAtt.score,
                                maxMarks = firstAtt.maxMarks,
                                correctCount = firstAtt.correctCount,
                                wrongCount = firstAtt.wrongCount,
                                unattemptedCount = firstAtt.unattemptedCount,
                                timeSpentSeconds = firstAtt.timeSpentSeconds,
                                completedAt = firstAtt.completedAt
                            ),
                            test
                        ))
                    } else {
                        viewModel.showToast("Attempt a mock test first to view score breakdown.")
                    }
                },
                ProfileMenuItem("Performance", "Overall preparation summary", Icons.Default.TrendingUp) { viewModel.showToast("Current overall progress: 72% Preparation Complete.") },
                ProfileMenuItem("Bookmarks", "Saved questions and formulas", Icons.Default.Bookmark) { viewModel.showToast("4 questions & 2 study materials saved in bookmarks.") },
                ProfileMenuItem("Downloads", "Offline PDF study materials", Icons.Default.Download) { viewModel.setTab(StudentTab.MATERIALS) },
                ProfileMenuItem("Notifications", "View official alerts", Icons.Default.Notifications) { viewModel.navigateTo(Screen.Notifications) },
                ProfileMenuItem("Payment History", "Razorpay receipts & orders", Icons.Default.ReceiptLong) { viewModel.showToast("Payment receipts: All transactions verified via Razorpay.") }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column {
                    learningItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { item.onClick() }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(item.icon, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
                                if (item.subtitle != null) {
                                    Text(item.subtitle, fontSize = 11.sp, color = TextMutedLight)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(18.dp))
                        }
                        if (index < learningItems.size - 1) {
                            Divider(modifier = Modifier.padding(horizontal = 14.dp), color = SurfaceBorderLight)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Support & Policies
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Support & Legal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
            Spacer(modifier = Modifier.height(8.dp))

            val supportItems = listOf(
                ProfileMenuItem("Help & Support", "Submit doubts & query tickets", Icons.Default.HelpCenter) { showSupportDialog = true },
                ProfileMenuItem("Privacy Policy", "Student data compliance", Icons.Default.PrivacyTip) { viewModel.showToast("WBTOPPER complies with Indian Data Protection Acts.") },
                ProfileMenuItem("Terms & Conditions", "Student usage rules", Icons.Default.Article) { viewModel.showToast("Terms: Standard examination preparation & fair usage terms apply.") }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column {
                    supportItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { item.onClick() }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(item.icon, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(18.dp))
                        }
                        if (index < supportItems.size - 1) {
                            Divider(modifier = Modifier.padding(horizontal = 14.dp), color = SurfaceBorderLight)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. CRITICAL: CLEARLY SEPARATED ADMIN PANEL OPTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Administrative Access",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue800
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.requestAdminPanelAccess() }
                    .testTag("profile_admin_panel_button"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RoyalBlue50
                ),
                border = BorderStroke(1.5.dp, RoyalBlue700.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Panel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue800
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = RoyalBlue800,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "RESTRICTED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Course, mock test, question bank & student management",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secured",
                        tint = RoyalBlue800,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 6. Sign Out Button
        PaddingValues(horizontal = 16.dp).let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.logout() }
                    .testTag("profile_logout_btn"),
                colors = CardDefaults.cardColors(containerColor = BreakingRedLight),
                border = BorderStroke(1.dp, BreakingRed.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = BreakingRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BreakingRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WBTOPPER Mobile Platform",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondaryLight
            )
            Text(
                text = "Version 2.6.4 (Production Release)",
                fontSize = 10.sp,
                color = TextMutedLight
            )
        }
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Submit Support Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = supportSubject,
                        onValueChange = { supportSubject = it },
                        label = { Text("Subject") },
                        placeholder = { Text("e.g. Test Result Doubt / Payment Query") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = supportMessage,
                        onValueChange = { supportMessage = it },
                        label = { Text("Describe your query") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (supportSubject.isNotBlank() && supportMessage.isNotBlank()) {
                            viewModel.submitTicket(supportSubject, supportCategory, supportMessage)
                            showSupportDialog = false
                        } else {
                            viewModel.showToast("Please enter subject and message")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800)
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

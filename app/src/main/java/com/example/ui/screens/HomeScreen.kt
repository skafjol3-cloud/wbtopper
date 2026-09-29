package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ExamCategory
import com.example.data.repository.ExamDataProvider
import com.example.ui.components.BreakingAlertBanner
import com.example.ui.components.BreakingNewsTicker
import com.example.ui.components.ExamCategoryCard
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

data class QuickAccessItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val tabOrAction: () -> Unit
)

@Composable
fun HomeScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val mockTests by viewModel.mockTests.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val studentName by viewModel.studentName.collectAsState()
    val attempts by viewModel.testAttempts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var dailySelectedOption by remember { mutableStateOf<Int?>(null) }
    val dailyQ = ExamDataProvider.sampleQuestions.first()

    // Promotional Banners Carousel Data
    val banners = listOf(
        Triple(
            "WBHRB Staff Nurse Grade II 2026",
            "Prepare Smarter. Score Higher.",
            "Full Clinical Syllabus, Mock Series & Live Notes"
        ),
        Triple(
            "ANM GNM 2026 Ranker Batch",
            "Target West Bengal Top Ranks",
            "10-Year Solved Questions & Doubt Solving"
        ),
        Triple(
            "AIIMS NORCET 10 Intensive",
            "All-India Nursing Officer Selection",
            "Clinical Triage, ECG & High-Yield MCQs"
        )
    )
    var currentBannerIndex by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // 1. Premium Search Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.setTab(StudentTab.COURSES) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = RoyalBlue700,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search exams, mock tests, courses...",
                        fontSize = 13.sp,
                        color = TextMutedLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. Breaking News Ticker
        item {
            BreakingNewsTicker(
                headline = "WBHRB Staff Nurse Grade II 2026 Notification Released! Start your preparation now.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // 3. Image-inspired Red Alert Banner
        item {
            Spacer(modifier = Modifier.height(10.dp))
            BreakingAlertBanner(
                title = "JENPAS-UG 2026 Admit Card Released",
                subtitle = "Tap to view all alerts",
                onClick = { viewModel.navigateTo(Screen.Notifications) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 4. Exam Categories Section (Exactly as shown in provided image)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Exam Categories",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Choose your target exam",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            var selectedFilter by remember { mutableStateOf("All") }
            val filterOptions = listOf("All", "Nursing Entrance", "Staff Nurse Jobs", "Allied & Entrance")

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        color = if (isSelected) RoyalBlue800 else SurfaceWhite,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue800 else SurfaceBorderLight),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SurfaceWhite else TextPrimaryLight,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                val filteredCategories = ExamDataProvider.examCategories.filter { cat ->
                    when (selectedFilter) {
                        "Nursing Entrance" -> cat.code in listOf("JENPAS_UG", "ANM_GNM", "AIIMS_NURSE", "JEMSCN", "JEPBN")
                        "Staff Nurse Jobs" -> cat.code in listOf("WBHRB_SN", "RRB_NURSE", "WB_CHO", "NORCET_10", "ESIC_NURSE", "PHARMACIST_GR3")
                        "Allied & Entrance" -> cat.code in listOf("CULET", "JECA", "JELET", "AIBE", "PARAMEDICAL")
                        else -> true
                    }
                }
                val pairs = filteredCategories.chunked(2)

                pairs.forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExamCategoryCard(
                            category = pair[0],
                            onClick = { viewModel.navigateTo(Screen.ExamDetail(pair[0].code)) },
                            modifier = Modifier.weight(1f)
                        )

                        if (pair.size > 1) {
                            ExamCategoryCard(
                                category = pair[1],
                                onClick = { viewModel.navigateTo(Screen.ExamDetail(pair[1].code)) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 5. Featured Promotional Banner Carousel
        item {
            Spacer(modifier = Modifier.height(24.dp))
            val (bTitle, bSubtitle, bDesc) = banners[currentBannerIndex]
            val bannerImageRes = when (currentBannerIndex) {
                0 -> R.drawable.img_hero_banner_art
                1 -> R.drawable.img_nursing_caduceus
                else -> R.drawable.img_topper_cup
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalBlue900, RoyalBlue800, IndigoPrimary)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = SurfaceWhite.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "FEATURED PREPARATION",
                                    color = SurfaceWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            // Dot indicators
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                banners.indices.forEach { index ->
                                    Box(
                                        modifier = Modifier
                                            .width(if (index == currentBannerIndex) 18.dp else 6.dp)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (index == currentBannerIndex) AccentAmber else SurfaceWhite.copy(alpha = 0.4f)
                                            )
                                            .clickable { currentBannerIndex = index }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bTitle,
                                    color = SurfaceWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )

                                Text(
                                    text = bSubtitle,
                                    color = AccentAmberLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                                )

                                Text(
                                    text = bDesc,
                                    color = RoyalBlue100,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.setTab(StudentTab.MOCK_TESTS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Start Practice",
                                        color = RoyalBlue800,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = RoyalBlue800,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Dashboard Hero Graphic Art
                            Box(
                                modifier = Modifier
                                    .size(105.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceWhite.copy(alpha = 0.12f))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(bannerImageRes),
                                    contentDescription = bTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Access Section (8 Items in 2-row horizontal grid)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Quick Access",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                val quickItems = listOf(
                    QuickAccessItem("Today's Practice", Icons.Default.FactCheck, RoyalBlue700) { viewModel.setTab(StudentTab.MOCK_TESTS) },
                    QuickAccessItem("Mock Tests", Icons.Default.Quiz, IndigoPrimary) { viewModel.setTab(StudentTab.MOCK_TESTS) },
                    QuickAccessItem("PYQ Papers", Icons.Default.HistoryEdu, PurpleAccent) { viewModel.setTab(StudentTab.MATERIALS) },
                    QuickAccessItem("Study Notes", Icons.Default.MenuBook, AccentAmber) { viewModel.setTab(StudentTab.MATERIALS) },
                    QuickAccessItem("Premium Batches", Icons.Default.WorkspacePremium, RoyalBlue800) { viewModel.setTab(StudentTab.COURSES) },
                    QuickAccessItem("My Results", Icons.Default.Analytics, SuccessGreen) { viewModel.setTab(StudentTab.PROFILE) },
                    QuickAccessItem("Performance", Icons.Default.TrendingUp, RoyalBlue600) { viewModel.setTab(StudentTab.PROFILE) },
                    QuickAccessItem("Bookmarks", Icons.Default.Bookmark, BreakingRed) { viewModel.setTab(StudentTab.PROFILE) }
                )

                // 4x2 Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickItems.chunked(4).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { item.tabOrAction() },
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(item.color.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = item.color,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimaryLight,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Study Progress Section with circular indicator and small performance chart
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
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
                        Column {
                            Text(
                                text = "Your Study Progress",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = "Keep practicing to improve your score.",
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }

                        // Topper Trophy and Circular Progress Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.img_topper_cup),
                                contentDescription = "Topper Trophy",
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { 0.72f },
                                    modifier = Modifier.size(50.dp),
                                    color = RoyalBlue700,
                                    trackColor = RoyalBlue50,
                                    strokeWidth = 5.dp
                                )
                                Text(
                                    text = "72%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalBlue800
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${attempts.size + 8}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                            Text("Tests Taken", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("78.5%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            Text("Avg Score", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("84%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue700)
                            Text("Accuracy", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("420+", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                            Text("Questions", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("7 Days", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                            Text("Streak", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Small Performance Bar Chart
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weekly Practice Trend", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                            Text("Goal: 50 Qs/day", fontSize = 11.sp, color = RoyalBlue700, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val days = listOf("Mon" to 0.45f, "Tue" to 0.70f, "Wed" to 0.60f, "Thu" to 0.85f, "Fri" to 0.90f, "Sat" to 0.75f, "Sun" to 0.95f)
                            days.forEach { (day, fraction) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height((fraction * 45).dp)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(if (fraction > 0.8f) RoyalBlue800 else RoyalBlue500.copy(alpha = 0.5f))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(day, fontSize = 9.sp, color = TextSecondaryLight)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Today's Practice Question
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question of the Day",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Surface(
                        color = AccentAmberLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "+1.0 Mark",
                            color = AccentAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            color = RoyalBlue50,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = dailyQ.subject,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue800,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = dailyQ.questionText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        dailyQ.options.forEachIndexed { idx, opt ->
                            val isSelected = dailySelectedOption == idx
                            val isCorrect = idx == dailyQ.correctOptionIndex
                            val showFeedback = dailySelectedOption != null

                            val bgColor = when {
                                showFeedback && isCorrect -> SuccessGreenLight
                                showFeedback && isSelected && !isCorrect -> BreakingRedLight
                                isSelected -> RoyalBlue100
                                else -> SurfaceCardLight
                            }

                            val borderColor = when {
                                showFeedback && isCorrect -> SuccessGreen
                                showFeedback && isSelected && !isCorrect -> BreakingRed
                                isSelected -> RoyalBlue700
                                else -> SurfaceBorderLight
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (dailySelectedOption == null) {
                                            dailySelectedOption = idx
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) RoyalBlue800 else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + idx).toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SurfaceWhite else TextPrimaryLight
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = opt,
                                        fontSize = 13.sp,
                                        color = TextPrimaryLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        if (dailySelectedOption != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = RoyalBlue50),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Explanation & Clinical Rationale:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(dailyQ.explanation, fontSize = 12.sp, color = TextPrimaryLight, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Featured Mock Tests
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Mock Tests",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                TextButton(onClick = { viewModel.setTab(StudentTab.MOCK_TESTS) }) {
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        color = RoyalBlue700,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockTests) { test ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(Screen.TestInstructions(test.id)) },
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = if (test.isFree) SuccessGreenLight else RoyalBlue50,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (test.isFree) "FREE MOCK" else "₹${test.price.toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (test.isFree) SuccessGreen else RoyalBlue800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = TextMutedLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${test.durationMinutes}m",
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = test.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${test.questions.size} Questions",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.TestInstructions(test.id)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Attempt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

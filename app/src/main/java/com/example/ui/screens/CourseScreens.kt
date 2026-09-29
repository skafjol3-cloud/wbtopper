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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

@Composable
fun CoursesScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.courses.collectAsState()
    var selectedCategoryTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val categoryTabs = listOf("Featured Courses", "Premium Courses", "Free Courses", "My Courses")

    val filtered = courses.filter { course ->
        val matchesCategory = when (selectedCategoryTab) {
            0 -> course.isFeatured
            1 -> course.isPremium
            2 -> course.price == 0.0
            3 -> course.isEnrolled
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() || course.title.contains(searchQuery, ignoreCase = true) || course.examCode.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(bottom = 76.dp)
            .testTag("courses_screen")
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search courses by exam or topic...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue700) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("courses_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = SurfaceWhite,
                focusedContainerColor = SurfaceWhite
            )
        )

        // Category Filter Chips
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryTab,
            containerColor = Color.Transparent,
            contentColor = RoyalBlue800,
            edgePadding = 16.dp,
            divider = {}
        ) {
            categoryTabs.forEachIndexed { index, tabTitle ->
                val isSelected = selectedCategoryTab == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategoryTab = index },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Surface(
                        color = if (isSelected) RoyalBlue800 else SurfaceWhite,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue800 else SurfaceBorderLight),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = tabTitle,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SurfaceWhite else TextPrimaryLight,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, contentDescription = null, tint = TextMutedLight, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No courses found in this category.", color = TextSecondaryLight, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filtered) { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.CourseDetail(course.id)) }
                            .testTag("course_card_${course.id}"),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Course Header Thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(RoyalBlue900, RoyalBlue700)
                                        )
                                    )
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        color = if (course.isEnrolled) SuccessGreen else AccentAmber,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (course.isEnrolled) "ENROLLED" else "FEATURED BATCH",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SurfaceWhite,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        color = SurfaceWhite.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("${course.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                                        }
                                    }
                                }

                                Text(
                                    text = course.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                            }

                            // Card Details
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = course.description,
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${course.totalLessons} Lessons", fontSize = 11.sp, color = TextSecondaryLight)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Assignment, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${course.totalMockTests} Mock Tests", fontSize = 11.sp, color = TextSecondaryLight)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = if (course.price == 0.0) "Free" else "₹${course.price.toInt()}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = RoyalBlue800
                                        )
                                        if (course.price > 0.0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "₹${course.originalPrice.toInt()}",
                                                fontSize = 12.sp,
                                                color = TextMutedLight,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (course.isEnrolled) {
                                                viewModel.navigateTo(Screen.CourseLearning(course.id))
                                            } else {
                                                viewModel.navigateTo(Screen.CourseDetail(course.id))
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (course.isEnrolled) SuccessGreen else RoyalBlue800
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (course.isEnrolled) "Continue" else "Enroll Now",
                                            fontSize = 12.sp,
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: ExamViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: courses.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Fee", fontSize = 11.sp, color = TextMutedLight)
                        Text(
                            text = if (course.price == 0.0) "Free" else "₹${course.price.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalBlue800
                        )
                    }

                    Button(
                        onClick = {
                            if (course.isEnrolled) {
                                viewModel.navigateTo(Screen.CourseLearning(course.id))
                            } else {
                                viewModel.enrollInCourse(course.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (course.isEnrolled) SuccessGreen else RoyalBlue800
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("course_action_button")
                    ) {
                        Text(
                            text = if (course.isEnrolled) "Open Course" else "Pay & Enroll (Razorpay)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
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
            Text(
                text = course.title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue800,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = course.description,
                fontSize = 13.sp,
                color = TextSecondaryLight,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue50),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = RoyalBlue700)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Curated by: ${course.instructor}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue800)
                        Text("Verified by WBTOPPER Academic Council", fontSize = 11.sp, color = TextSecondaryLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Course Curriculum (${course.lessons.size} Lessons)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )

            Spacer(modifier = Modifier.height(10.dp))

            course.lessons.forEachIndexed { idx, lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (lesson.isDemo) SuccessGreenLight else RoyalBlue50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (lesson.isDemo) Icons.Default.PlayArrow else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (lesson.isDemo) SuccessGreen else RoyalBlue800,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(lesson.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
                            Text("${lesson.chapterTitle} • ${lesson.duration}", fontSize = 11.sp, color = TextSecondaryLight)
                        }

                        if (lesson.isDemo) {
                            Surface(color = SuccessGreen, shape = RoundedCornerShape(4.dp)) {
                                Text("FREE DEMO", color = SurfaceWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
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
fun CourseLearningScreen(
    courseId: String,
    initialLessonIndex: Int = 0,
    viewModel: ExamViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: courses.first()

    var activeLessonIndex by remember { mutableStateOf(initialLessonIndex) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf("1.0x") }
    var showSpeedDialog by remember { mutableStateOf(false) }

    val currentLesson = course.lessons.getOrNull(activeLessonIndex) ?: course.lessons.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentLesson.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
        ) {
            // Simulated Video Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isPlaying) "Playing: ${currentLesson.title}" else "Tap to Play Lesson",
                        color = SurfaceWhite,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentLesson.duration, color = SurfaceWhite, fontSize = 11.sp)

                    Row {
                        TextButton(onClick = { showSpeedDialog = true }) {
                            Text(selectedSpeed, color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.showToast("Lesson bookmarked") }) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = SurfaceWhite, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(currentLesson.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Text(currentLesson.chapterTitle, fontSize = 12.sp, color = RoyalBlue700)

                Spacer(modifier = Modifier.height(16.dp))

                if (currentLesson.pdfNotesTitle.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.showToast("Opening attached notes: ${currentLesson.pdfNotesTitle}") },
                        colors = CardDefaults.cardColors(containerColor = RoyalBlue50)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BreakingRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(currentLesson.pdfNotesTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                                Text("Class Handout & Notes • PDF", fontSize = 10.sp, color = TextSecondaryLight)
                            }
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = RoyalBlue700)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Lessons in this Course", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)

                Spacer(modifier = Modifier.height(10.dp))

                course.lessons.forEachIndexed { idx, les ->
                    val isCurrent = idx == activeLessonIndex
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeLessonIndex = idx },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) RoyalBlue50 else SurfaceWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) RoyalBlue700 else SurfaceBorderLight)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx + 1}.", fontWeight = FontWeight.Bold, color = if (isCurrent) RoyalBlue700 else TextSecondaryLight)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(les.title, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                Text(les.duration, fontSize = 11.sp, color = TextMutedLight)
                            }
                            if (isCurrent) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Active", tint = RoyalBlue700, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Playback Speed") },
            text = {
                Column {
                    listOf("0.75x", "1.0x", "1.25x", "1.5x", "2.0x").forEach { spd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSpeed = spd
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedSpeed == spd, onClick = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(spd, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

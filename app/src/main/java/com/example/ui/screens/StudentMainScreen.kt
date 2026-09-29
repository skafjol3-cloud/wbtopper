package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AdminAuthModal
import com.example.ui.components.BanglaExamTopBar
import com.example.ui.components.StudentBottomNavBar
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StudentTab

@Composable
fun StudentMainScreen(
    viewModel: ExamViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val studentName by viewModel.studentName.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    val adminAuthOpen by viewModel.adminAuthDialogOpen.collectAsState()
    val adminAuthError by viewModel.adminAuthError.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            BanglaExamTopBar(
                studentName = studentName,
                unreadNotificationCount = unreadCount,
                onNotificationClick = { viewModel.navigateTo(Screen.Notifications) },
                onProfileClick = { viewModel.setTab(StudentTab.PROFILE) }
            )
        },
        bottomBar = {
            StudentBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_main_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                StudentTab.HOME -> HomeScreen(viewModel = viewModel)
                StudentTab.COURSES -> CoursesScreen(viewModel = viewModel)
                StudentTab.MOCK_TESTS -> MockTestsScreen(viewModel = viewModel)
                StudentTab.MATERIALS -> StudyMaterialsScreen(viewModel = viewModel)
                StudentTab.PROFILE -> StudentProfileScreen(viewModel = viewModel)
            }
        }
    }

    // Secure Admin Authorization Modal (Triggered from Profile -> Admin Panel)
    AdminAuthModal(
        isOpen = adminAuthOpen,
        errorMessage = adminAuthError,
        onDismiss = { viewModel.closeAdminAuthDialog() },
        onVerify = { email, pass -> viewModel.verifyAdminCredentials(email, pass) }
    )
}

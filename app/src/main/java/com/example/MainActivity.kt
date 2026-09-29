package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AdminAuthModal
import com.example.ui.screens.*
import com.example.ui.theme.BanglaExamTheme
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BanglaExamTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val adminAuthOpen by viewModel.adminAuthDialogOpen.collectAsState()
                val adminAuthError by viewModel.adminAuthError.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val screen = currentScreen) {
                        is Screen.Splash -> SplashScreen(viewModel)
                        is Screen.Login -> LoginScreen(viewModel)
                        is Screen.SignUp -> SignUpScreen(viewModel)
                        is Screen.ForgotPassword -> ForgotPasswordScreen(viewModel)
                        is Screen.StudentMain -> StudentMainScreen(viewModel)
                        is Screen.Notifications -> NotificationsScreen(viewModel)
                        is Screen.ExamDetail -> ExamDetailScreen(screen.examCode, viewModel)
                        is Screen.CourseDetail -> CourseDetailScreen(screen.courseId, viewModel)
                        is Screen.CourseLearning -> CourseLearningScreen(screen.courseId, screen.lessonIndex, viewModel)
                        is Screen.TestInstructions -> TestInstructionsScreen(screen.testId, viewModel)
                        is Screen.LiveTest -> LiveTestScreen(screen.testId, viewModel)
                        is Screen.TestResult -> TestResultScreen(screen.attempt, screen.test, viewModel)
                        is Screen.PdfViewer -> PdfViewerScreen(screen.materialId, viewModel)
                        is Screen.AdminPortal -> AdminPortalScreen(viewModel)
                    }

                    // Global Admin Auth dialog if opened outside StudentMain
                    if (currentScreen !is Screen.StudentMain) {
                        AdminAuthModal(
                            isOpen = adminAuthOpen,
                            errorMessage = adminAuthError,
                            onDismiss = { viewModel.closeAdminAuthDialog() },
                            onVerify = { email, pass -> viewModel.verifyAdminCredentials(email, pass) }
                        )
                    }
                }
            }
        }
    }
}

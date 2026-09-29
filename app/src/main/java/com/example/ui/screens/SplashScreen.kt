package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: ExamViewModel
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        delay(1500)
        if (viewModel.currentScreen.value == Screen.Splash) {
            if (isAuthenticated) {
                viewModel.navigateTo(Screen.StudentMain)
            } else {
                viewModel.navigateTo(Screen.Login)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(RoyalBlue900, RoyalBlue800, IndigoPrimary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalBlue700, IndigoPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "WBTOPPER Logo",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WBTOPPER",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SurfaceWhite,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Premier Nursing & Competitive Exam Platform",
                fontSize = 13.sp,
                color = RoyalBlue100.copy(alpha = 0.9f),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = SurfaceWhite,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Official Educational Mobile Edition",
            fontSize = 11.sp,
            color = RoyalBlue100.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        )
    }
}

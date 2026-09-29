package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.StudentTab

@Composable
fun AdminAuthModal(
    isOpen: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onVerify: (String, String) -> Unit
) {
    if (!isOpen) return

    var emailOrKey by remember { mutableStateOf("afjolsk0@gmail.com") }
    var password by remember { mutableStateOf("afjol@123") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Security",
                        tint = RoyalBlue800,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Admin Authorization",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Text(
                    text = "Administrative access requires authorized Super Admin credentials. All session events are monitored.",
                    fontSize = 12.sp,
                    color = TextSecondaryLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = emailOrKey,
                    onValueChange = { emailOrKey = it },
                    label = { Text("Admin Email / Security Key") },
                    placeholder = { Text("afjolsk0@gmail.com") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = RoyalBlue700)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Admin Password") },
                    placeholder = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue700)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = BreakingRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_cancel_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onVerify(emailOrKey, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_verify_btn")
                    ) {
                        Text("Verify Role")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentBottomNavBar(
    selectedTab: StudentTab,
    onTabSelected: (StudentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("student_bottom_navigation"),
        containerColor = SurfaceWhite,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == StudentTab.HOME,
            onClick = { onTabSelected(StudentTab.HOME) },
            icon = {
                Icon(
                    if (selectedTab == StudentTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = if (selectedTab == StudentTab.HOME) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue800,
                selectedTextColor = RoyalBlue800,
                indicatorColor = RoyalBlue100
            )
        )

        NavigationBarItem(
            selected = selectedTab == StudentTab.COURSES,
            onClick = { onTabSelected(StudentTab.COURSES) },
            icon = {
                Icon(
                    if (selectedTab == StudentTab.COURSES) Icons.Filled.PlayLesson else Icons.Outlined.PlayLesson,
                    contentDescription = "Courses"
                )
            },
            label = { Text("Courses", fontSize = 11.sp, fontWeight = if (selectedTab == StudentTab.COURSES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue800,
                selectedTextColor = RoyalBlue800,
                indicatorColor = RoyalBlue100
            )
        )

        NavigationBarItem(
            selected = selectedTab == StudentTab.MOCK_TESTS,
            onClick = { onTabSelected(StudentTab.MOCK_TESTS) },
            icon = {
                Icon(
                    if (selectedTab == StudentTab.MOCK_TESTS) Icons.Filled.Quiz else Icons.Outlined.Quiz,
                    contentDescription = "Mock Tests"
                )
            },
            label = { Text("Mock Tests", fontSize = 11.sp, fontWeight = if (selectedTab == StudentTab.MOCK_TESTS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue800,
                selectedTextColor = RoyalBlue800,
                indicatorColor = RoyalBlue100
            )
        )

        NavigationBarItem(
            selected = selectedTab == StudentTab.MATERIALS,
            onClick = { onTabSelected(StudentTab.MATERIALS) },
            icon = {
                Icon(
                    if (selectedTab == StudentTab.MATERIALS) Icons.Filled.Description else Icons.Outlined.Description,
                    contentDescription = "Study Materials"
                )
            },
            label = { Text("Materials", fontSize = 11.sp, fontWeight = if (selectedTab == StudentTab.MATERIALS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue800,
                selectedTextColor = RoyalBlue800,
                indicatorColor = RoyalBlue100
            )
        )

        NavigationBarItem(
            selected = selectedTab == StudentTab.PROFILE,
            onClick = { onTabSelected(StudentTab.PROFILE) },
            icon = {
                Icon(
                    if (selectedTab == StudentTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = if (selectedTab == StudentTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue800,
                selectedTextColor = RoyalBlue800,
                indicatorColor = RoyalBlue100
            )
        )
    }
}

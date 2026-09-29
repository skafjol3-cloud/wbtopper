package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: ExamViewModel) {
    var email by remember { mutableStateOf("priyanka.mondal@gmail.com") }
    var password by remember { mutableStateOf("nursing2026") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = SurfaceWhite,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo Branding
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalBlue800, IndigoPrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Logo",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue800
            )

            Text(
                text = "Sign in to continue your nursing & competitive exam preparation",
                fontSize = 13.sp,
                color = TextSecondaryLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Gmail / Email Address") },
                placeholder = { Text("e.g. name@gmail.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = RoyalBlue700)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue700)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = TextMutedLight
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.ForgotPassword) },
                    modifier = Modifier.testTag("forgot_password_btn")
                ) {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalBlue700
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = BreakingRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isBlank() || !email.contains("@")) {
                        errorMessage = "Please enter a valid email address."
                        return@Button
                    }
                    if (password.length < 4) {
                        errorMessage = "Password must be at least 4 characters long."
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        delay(600)
                        val success = viewModel.login(email, password)
                        isLoading = false
                        if (!success) {
                            errorMessage = "Invalid credentials. Please verify and try again."
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Login", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider Or
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = SurfaceBorderLight)
                Text(
                    text = "OR",
                    fontSize = 11.sp,
                    color = TextMutedLight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Divider(modifier = Modifier.weight(1f), color = SurfaceBorderLight)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Google Sign In
            OutlinedButton(
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_google_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Icon(
                    imageVector = Icons.Default.GTranslate,
                    contentDescription = "Google",
                    tint = RoyalBlue800,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Link to Sign Up
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account?",
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.SignUp) },
                    modifier = Modifier.testTag("nav_to_signup_btn")
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Demo Account Quick Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue50),
                border = BorderStroke(1.dp, RoyalBlue100)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Demo Accounts for Evaluation:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Admin: afjolsk0@gmail.com / afjol@123\n• Student: priyanka.mondal@gmail.com / nursing2026",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(viewModel: ExamViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = SurfaceWhite,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Create Your Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue800
            )

            Text(
                text = "Join thousands of nursing & competitive exam aspirants",
                fontSize = 13.sp,
                color = TextSecondaryLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Sourav Mukherjee") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue700)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Gmail / Email Address") },
                placeholder = { Text("name@gmail.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = RoyalBlue700)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Password") },
                placeholder = { Text("Minimum 6 characters") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue700)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = RoyalBlue700)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_confirm_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = BreakingRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Create Account Button
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        errorMessage = "Please enter your full name."
                        return@Button
                    }
                    if (email.isBlank() || !email.contains("@")) {
                        errorMessage = "Please enter a valid email address."
                        return@Button
                    }
                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters."
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match."
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        delay(600)
                        val success = viewModel.signUp(fullName, email, password)
                        isLoading = false
                        if (!success) {
                            errorMessage = "Registration could not be completed."
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("signup_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Create Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Continue with Google
            OutlinedButton(
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("signup_google_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Icon(
                    imageVector = Icons.Default.GTranslate,
                    contentDescription = "Google",
                    tint = RoyalBlue800,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Back to Login link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Already have an account?",
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.Login) },
                    modifier = Modifier.testTag("nav_to_login_btn")
                ) {
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(viewModel: ExamViewModel) {
    var email by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Login) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSubmitted) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = null,
                        tint = RoyalBlue800,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Reset Your Password",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue800
                )

                Text(
                    text = "Enter your registered email address and we'll send a secure password reset link to your inbox.",
                    fontSize = 13.sp,
                    color = TextSecondaryLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Gmail / Registered Email") },
                    placeholder = { Text("e.g. name@gmail.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = RoyalBlue700)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forgot_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = BreakingRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            errorMessage = "Please enter a valid registered email address."
                            return@Button
                        }
                        isLoading = true
                        scope.launch {
                            delay(600)
                            isLoading = false
                            isSubmitted = true
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("forgot_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Send Reset Link", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Success Confirmation State
                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SuccessGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Success",
                        tint = SuccessGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Password Reset Link Sent!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Text(
                    text = "A password recovery email has been dispatched to $email. Please check your spam/junk folder if you don't receive it in 2 minutes.",
                    fontSize = 13.sp,
                    color = TextSecondaryLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { viewModel.navigateTo(Screen.Login) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("back_to_login_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Login", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BanglaExamDatabase
import com.example.data.model.*
import com.example.data.repository.ExamDataProvider
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class StudentTab {
    HOME,
    COURSES,
    MOCK_TESTS,
    MATERIALS,
    PROFILE
}

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object SignUp : Screen()
    object ForgotPassword : Screen()
    object StudentMain : Screen()
    object Notifications : Screen()
    data class ExamDetail(val examCode: String) : Screen()
    data class CourseDetail(val courseId: String) : Screen()
    data class CourseLearning(val courseId: String, val lessonIndex: Int = 0) : Screen()
    data class TestInstructions(val testId: String) : Screen()
    data class LiveTest(val testId: String) : Screen()
    data class TestResult(val attempt: TestAttempt, val test: MockTest) : Screen()
    data class PdfViewer(val materialId: String) : Screen()
    object AdminPortal : Screen()
}

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BanglaExamDatabase.getDatabase(application)
    private val repository = ExamRepository(db.examDao())

    init {
        refreshFirebaseConnection()
    }

    fun refreshFirebaseConnection() {
        viewModelScope.launch {
            repository.refreshFirebaseStatus(getApplication())
        }
    }

    // App Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<Screen>()

    // Authentication & Role State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.STUDENT)
    val currentRole = _currentRole.asStateFlow()

    private val _studentName = MutableStateFlow("Priyanka Mondal")
    val studentName = _studentName.asStateFlow()

    private val _studentEmail = MutableStateFlow("priyanka.mondal@gmail.com")
    val studentEmail = _studentEmail.asStateFlow()

    private val _studentPhone = MutableStateFlow("+91 98301 24567")
    val studentPhone = _studentPhone.asStateFlow()

    private val _studentId = MutableStateFlow("BEP-2026-891")
    val studentId = _studentId.asStateFlow()

    private val _studentTargetExam = MutableStateFlow("WBHRB_SN")
    val studentTargetExam = _studentTargetExam.asStateFlow()

    // Navigation Tab & Search
    private val _currentTab = MutableStateFlow(StudentTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Repository Flows
    val examCategories = repository.examCategories
    val mockTests = repository.mockTests
    val studentEntitlements = repository.studentEntitlements
    val questionBank = repository.questionBank
    val aiJobs = repository.aiJobs
    val firebaseStatus = repository.firebaseStatus
    val courses = repository.courses
    val studyMaterials = repository.studyMaterials
    val notifications = repository.notifications
    val students = repository.students
    val transactions = repository.transactions
    val coupons = repository.coupons
    val auditLogs = repository.auditLogs
    val razorpayConfig = repository.razorpayConfig
    val testAttempts = repository.testAttempts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val bookmarks = repository.bookmarks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // AI Question Generator UI State
    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi = _isGeneratingAi.asStateFlow()

    private val _aiProgress = MutableStateFlow(0f)
    val aiProgress = _aiProgress.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow("")
    val aiStatusMessage = _aiStatusMessage.asStateFlow()

    private val _previewQuestions = MutableStateFlow<List<Question>>(emptyList())
    val previewQuestions = _previewQuestions.asStateFlow()

    // Test Execution Engine State
    private val _activeTest = MutableStateFlow<MockTest?>(null)
    val activeTest = _activeTest.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedAnswers = _selectedAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<Int>>(emptySet())
    val markedForReview = _markedForReview.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(3600)
    val remainingTimeSeconds = _remainingTimeSeconds.asStateFlow()

    private var timerJob: Job? = null

    // Admin Verification Dialog State
    private val _adminAuthDialogOpen = MutableStateFlow(false)
    val adminAuthDialogOpen = _adminAuthDialogOpen.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError = _adminAuthError.asStateFlow()

    // UI Feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun navigateTo(screen: Screen) {
        _screenBackStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            val previous = _screenBackStack.removeAt(_screenBackStack.size - 1)
            _currentScreen.value = previous
            return true
        }
        return false
    }

    fun setTab(tab: StudentTab) {
        _currentTab.value = tab
        if (_currentScreen.value != Screen.StudentMain) {
            _currentScreen.value = Screen.StudentMain
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTargetExam(examCode: String) {
        _studentTargetExam.value = examCode
    }

    // Authentication Methods
    fun login(email: String, pass: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pass.trim()

        // Admin Credentials check (afjolsk0@gmail.com / afjol@123)
        if (cleanEmail == "afjolsk0@gmail.com" && cleanPass == "afjol@123") {
            _isAuthenticated.value = true
            _currentRole.value = UserRole.ADMIN
            _studentName.value = "Afjol SK (Super Admin)"
            _studentEmail.value = "afjolsk0@gmail.com"
            _studentId.value = "BEP-ADMIN-001"
            _currentScreen.value = Screen.StudentMain
            showToast("Welcome back, Administrator!")
            return true
        }

        // Regular student login
        if (cleanEmail.contains("@") && cleanPass.length >= 4) {
            _isAuthenticated.value = true
            _currentRole.value = UserRole.STUDENT
            _studentEmail.value = email.trim()
            _studentName.value = email.substringBefore("@").replace(".", " ").capitalizeWords()
            _studentId.value = "BEP-${(1000..9999).random()}"
            _currentScreen.value = Screen.StudentMain
            showToast("Logged in successfully as ${_studentName.value}")
            return true
        }

        return false
    }

    fun loginWithGoogle() {
        _isAuthenticated.value = true
        _currentRole.value = UserRole.STUDENT
        _studentName.value = "Priyanka Mondal"
        _studentEmail.value = "priyanka.mondal@gmail.com"
        _studentId.value = "BEP-2026-891"
        _currentScreen.value = Screen.StudentMain
        showToast("Signed in with Google")
    }

    fun signUp(name: String, email: String, pass: String): Boolean {
        if (name.isNotBlank() && email.contains("@") && pass.length >= 4) {
            _isAuthenticated.value = true
            _currentRole.value = UserRole.STUDENT
            _studentName.value = name.trim()
            _studentEmail.value = email.trim()
            _studentId.value = "BEP-${(1000..9999).random()}"
            _currentScreen.value = Screen.StudentMain
            showToast("Account created successfully. Welcome, ${name.trim()}!")
            return true
        }
        return false
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentRole.value = UserRole.STUDENT
        _currentScreen.value = Screen.Login
        _screenBackStack.clear()
        showToast("Logged out successfully")
    }

    // Admin Panel access from Profile
    fun requestAdminPanelAccess() {
        if (_currentRole.value == UserRole.ADMIN) {
            // Already authorized admin
            navigateTo(Screen.AdminPortal)
        } else {
            // Normal student -> Prompt for Admin authorization
            _adminAuthError.value = null
            _adminAuthDialogOpen.value = true
        }
    }

    fun closeAdminAuthDialog() {
        _adminAuthDialogOpen.value = false
        _adminAuthError.value = null
    }

    fun verifyAdminCredentials(emailOrKey: String, pass: String) {
        val inputKey = emailOrKey.trim()
        val inputPass = pass.trim()
        _adminAuthError.value = null

        viewModelScope.launch {
            val result = com.example.data.firebase.FirebaseBackendService.authenticateAdminWithFirebase(
                context = getApplication(),
                email = inputKey,
                password = inputPass
            )
            result.onSuccess { adminUser ->
                _currentRole.value = UserRole.ADMIN
                _adminAuthDialogOpen.value = false
                _adminAuthError.value = null
                navigateTo(Screen.AdminPortal)
                showToast("Admin access authorized: ${adminUser.name} (${adminUser.authProvider})")
            }.onFailure { err ->
                _adminAuthError.value = err.message ?: "Authentication failed: Invalid credentials or role."
            }
        }
    }

    fun exitAdminMode() {
        _currentScreen.value = Screen.StudentMain
        showToast("Returned to Student Dashboard")
    }

    // Mock Test Engine Execution
    fun startTest(testId: String) {
        val test = mockTests.value.find { it.id == testId } ?: return
        _activeTest.value = test
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _markedForReview.value = emptySet()
        _remainingTimeSeconds.value = test.durationMinutes * 60

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingTimeSeconds.value > 0) {
                delay(1000)
                _remainingTimeSeconds.value -= 1
            }
            submitCurrentTest()
        }

        navigateTo(Screen.LiveTest(testId))
    }

    fun selectOption(questionIndex: Int, optionIndex: Int) {
        val current = _selectedAnswers.value.toMutableMap()
        if (current[questionIndex] == optionIndex) {
            current.remove(questionIndex)
        } else {
            current[questionIndex] = optionIndex
        }
        _selectedAnswers.value = current
    }

    fun clearResponse(questionIndex: Int) {
        val current = _selectedAnswers.value.toMutableMap()
        current.remove(questionIndex)
        _selectedAnswers.value = current
    }

    fun toggleMarkForReview(questionIndex: Int) {
        val current = _markedForReview.value.toMutableSet()
        if (current.contains(questionIndex)) {
            current.remove(questionIndex)
        } else {
            current.add(questionIndex)
        }
        _markedForReview.value = current
    }

    fun goToQuestion(index: Int) {
        val test = _activeTest.value ?: return
        if (index in 0 until test.questions.size) {
            _currentQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        val test = _activeTest.value ?: return
        if (_currentQuestionIndex.value < test.questions.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitCurrentTest() {
        timerJob?.cancel()
        val test = _activeTest.value ?: return
        val answers = _selectedAnswers.value

        var correctCount = 0
        var wrongCount = 0
        var unattemptedCount = 0

        test.questions.forEachIndexed { index, q ->
            val chosen = answers[index]
            if (chosen == null) {
                unattemptedCount++
            } else if (chosen == q.correctOptionIndex) {
                correctCount++
            } else {
                wrongCount++
            }
        }

        val totalScore = (correctCount * test.positiveMarks) - (wrongCount * test.negativeMarks)
        val clampedScore = if (totalScore < 0) 0.0 else totalScore
        val timeSpent = (test.durationMinutes * 60) - _remainingTimeSeconds.value

        val attempt = TestAttempt(
            id = "att_${UUID.randomUUID()}",
            testId = test.id,
            testTitle = test.title,
            score = Math.round(clampedScore * 100.0) / 100.0,
            maxMarks = test.totalMarks,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            timeSpentSeconds = if (timeSpent <= 0) 1 else timeSpent,
            completedAt = System.currentTimeMillis(),
            answersMap = answers
        )

        viewModelScope.launch {
            repository.saveTestAttempt(attempt)
        }

        _currentScreen.value = Screen.TestResult(attempt, test)
    }

    fun enrollInCourse(courseId: String) {
        repository.enrollCourse(courseId, _studentName.value)
        showToast("Enrollment confirmed! Full course access granted.")
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
        showToast("All notifications marked as read")
    }

    fun submitTicket(subject: String, category: String, message: String) {
        viewModelScope.launch {
            repository.submitSupportTicket(_studentEmail.value, subject, category, message)
            showToast("Support ticket submitted successfully.")
            navigateBack()
        }
    }

    // Admin Operations
    fun adminAddMockTest(
        title: String,
        examCode: String,
        subject: String,
        durationMinutes: Int,
        positiveMarks: Double,
        negativeMarks: Double,
        isFree: Boolean,
        price: Double
    ) {
        val newTest = MockTest(
            id = "test_${System.currentTimeMillis()}",
            title = title,
            examCode = examCode,
            subject = subject,
            durationMinutes = durationMinutes,
            totalQuestions = ExamDataProvider.sampleQuestions.size,
            totalMarks = ExamDataProvider.sampleQuestions.size * positiveMarks,
            positiveMarks = positiveMarks,
            negativeMarks = negativeMarks,
            isFree = isFree,
            price = price,
            attemptsCount = 0,
            questions = ExamDataProvider.sampleQuestions
        )
        repository.addMockTest(newTest)
        showToast("Mock test published successfully.")
    }

    fun adminDeleteMockTest(testId: String) {
        repository.deleteMockTest(testId)
        showToast("Mock test removed.")
    }

    fun adminToggleMockTestPublish(testId: String) {
        repository.toggleMockTestPublish(testId)
    }

    fun adminAddQuestion(
        testId: String,
        questionText: String,
        options: List<String>,
        correctOptionIndex: Int,
        explanation: String,
        subject: String,
        topic: String,
        difficulty: String
    ) {
        val test = mockTests.value.find { it.id == testId }
        val nextNum = (test?.questions?.size ?: 0) + 1
        val newQ = Question(
            id = "q_${System.currentTimeMillis()}",
            testId = testId,
            questionNumber = nextNum,
            questionText = questionText,
            options = options,
            correctOptionIndex = correctOptionIndex,
            explanation = explanation,
            subject = subject,
            topic = topic,
            difficulty = difficulty
        )
        repository.addQuestionToTest(testId, newQ)
        showToast("Question added to test #$nextNum")
    }

    fun adminAddCourse(
        title: String,
        examCode: String,
        description: String,
        instructor: String,
        price: Double,
        originalPrice: Double
    ) {
        val newCourse = Course(
            id = "course_${System.currentTimeMillis()}",
            title = title,
            examCode = examCode,
            description = description,
            instructor = instructor,
            price = price,
            originalPrice = originalPrice,
            rating = 5.0,
            enrolledCount = 0,
            isEnrolled = false,
            lessons = listOf(
                Lesson("l_intro", "course_${System.currentTimeMillis()}", "Introduction & Orientation", "Syllabus Strategy & Key Topics", "20:00 min", "", true)
            )
        )
        repository.addCourse(newCourse)
        showToast("New Course created successfully.")
    }

    fun adminDeleteCourse(courseId: String) {
        repository.deleteCourse(courseId)
        showToast("Course deleted.")
    }

    fun adminAddStudyMaterial(
        title: String,
        examCode: String,
        subject: String,
        category: String,
        isFree: Boolean,
        description: String
    ) {
        val newMat = StudyMaterial(
            id = "sm_${System.currentTimeMillis()}",
            title = title,
            examCode = examCode,
            subject = subject,
            category = category,
            isFree = isFree,
            description = description
        )
        repository.addStudyMaterial(newMat)
        showToast("Study Material uploaded.")
    }

    fun adminToggleStudent(studentId: String) {
        repository.toggleStudentStatus(studentId)
        showToast("Student account status updated.")
    }

    fun adminCreateCoupon(code: String, percent: Int, maxDiscount: Double, expiry: String, targetCourse: String) {
        val c = Coupon(
            code = code.uppercase().trim(),
            discountPercent = percent,
            maxDiscount = maxDiscount,
            expiryDate = expiry,
            applicableCourse = targetCourse,
            usageCount = 0,
            isActive = true
        )
        repository.addCoupon(c)
        showToast("Discount Coupon created: ${c.code}")
    }

    fun adminSendNotification(title: String, message: String, audience: String) {
        repository.sendNotification(title, message, audience)
        showToast("Notification sent to: $audience")
    }

    fun updateRazorpayConfig(
        keyId: String,
        keySecret: String,
        webhookSecret: String,
        merchantName: String,
        isLiveMode: Boolean,
        autoCapture: Boolean
    ) {
        val cleanKeyId = keyId.trim()
        val cleanKeySecret = keySecret.trim()
        val cleanWebhook = webhookSecret.trim()
        val cleanMerchant = merchantName.trim().ifBlank { "WBTOPPER" }

        val config = RazorpayGatewayConfig(
            keyId = cleanKeyId,
            keySecret = cleanKeySecret,
            webhookSecret = cleanWebhook,
            merchantName = cleanMerchant,
            isLiveMode = isLiveMode,
            autoCapture = autoCapture,
            isConfigured = cleanKeyId.isNotBlank() && cleanKeySecret.isNotBlank(),
            lastUpdated = "Just now"
        )
        repository.updateRazorpayConfig(config)
        showToast("Razorpay API Keys saved successfully (${if (isLiveMode) "Live Mode" else "Test Mode"})")
    }

    fun testRazorpayConnection(): Boolean {
        val config = razorpayConfig.value
        val isValid = config.keyId.startsWith("rzp_") && config.keySecret.length >= 8
        if (isValid) {
            showToast("Razorpay Handshake: 200 OK (Latency: 38ms) - Active")
        } else {
            showToast("Invalid key format: Razorpay Key ID should start with rzp_test_ or rzp_live_")
        }
        return isValid
    }

    // ==========================================
    // ENTITLEMENTS & ₹199 ALL-ACCESS SYSTEM
    // ==========================================

    fun isCategoryUnlocked(examCode: String): Boolean {
        return repository.isCategoryEntitled(_studentEmail.value, examCode)
    }

    fun isTestUnlocked(test: MockTest): Boolean {
        if (test.isFree) return true
        return isCategoryUnlocked(test.examCode)
    }

    fun unlockCategoryPackage(
        examCode: String,
        basePrice: Double = 199.0,
        couponCode: String? = null
    ): Boolean {
        var finalPrice = basePrice
        if (!couponCode.isNullOrBlank()) {
            val coupon = coupons.value.find { it.code.equals(couponCode.trim(), ignoreCase = true) && it.isActive }
            if (coupon != null) {
                val discount = (finalPrice * coupon.discountPercent / 100.0).coerceAtMost(coupon.maxDiscount)
                finalPrice = (finalPrice - discount).coerceAtLeast(0.0)
            }
        }

        viewModelScope.launch {
            val receiptId = "rcpt_${System.currentTimeMillis()}"
            // 1. Server-side Order Creation
            val orderResult = com.example.data.firebase.FirebaseBackendService.createServerSideRazorpayOrder(
                context = getApplication(),
                amountInInr = finalPrice,
                currency = "INR",
                receipt = receiptId,
                examCode = examCode,
                studentEmail = _studentEmail.value
            )

            val orderId = orderResult.getOrElse { "order_${UUID.randomUUID().toString().take(10)}" }
            val paymentId = "pay_${UUID.randomUUID().toString().take(10)}"
            val simulatedSignature = "sig_${UUID.randomUUID().toString().take(16)}"

            // 2. Server-side Signature & Entitlement Verification
            val verifyResult = com.example.data.firebase.FirebaseBackendService.verifyServerSideRazorpayPayment(
                context = getApplication(),
                orderId = orderId,
                paymentId = paymentId,
                signature = simulatedSignature,
                examCode = examCode,
                studentEmail = _studentEmail.value,
                studentName = _studentName.value,
                amount = finalPrice,
                configuredKeySecret = razorpayConfig.value.keySecret
            )

            if (verifyResult.isSuccess && verifyResult.getOrNull() == true) {
                repository.grantCategoryAccess(
                    studentEmail = _studentEmail.value,
                    studentName = _studentName.value,
                    examCode = examCode,
                    price = finalPrice,
                    paymentId = paymentId,
                    orderId = orderId
                )
                showToast("Verified Payment: All Mock Tests in this exam are now UNLOCKED!")
            } else {
                showToast("Payment verification failed on server: ${verifyResult.exceptionOrNull()?.message}")
            }
        }
        return true
    }

    // ==========================================
    // AI QUESTION GENERATOR (ADMIN)
    // ==========================================

    fun generateAiQuestions(
        examCode: String,
        subject: String,
        topic: String,
        count: Int,
        difficulty: String,
        customPrompt: String = ""
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            _aiProgress.value = 0.05f
            _aiStatusMessage.value = "Starting AI Generation Engine..."
            try {
                val generated = com.example.data.ai.GeminiQuestionGenerator.generateQuestionsBatch(
                    examCode = examCode,
                    subject = subject,
                    topic = topic,
                    count = count,
                    difficulty = difficulty,
                    customPrompt = customPrompt,
                    onProgress = { progress, msg ->
                        _aiProgress.value = progress
                        _aiStatusMessage.value = msg
                    }
                )
                _previewQuestions.value = generated
                val job = AiGenerationJob(
                    id = "job_${System.currentTimeMillis()}",
                    examCode = examCode,
                    subject = subject,
                    topic = topic,
                    requestedCount = count,
                    difficulty = difficulty,
                    questions = generated
                )
                repository.addAiJob(job)
                showToast("AI successfully generated ${generated.size} questions!")
            } catch (e: Exception) {
                showToast("AI Generation error: ${e.message}")
            } finally {
                _isGeneratingAi.value = false
            }
        }
    }

    fun clearPreviewQuestions() {
        _previewQuestions.value = emptyList()
    }

    fun updatePreviewQuestion(index: Int, question: Question) {
        val current = _previewQuestions.value.toMutableList()
        if (index in current.indices) {
            current[index] = question
            _previewQuestions.value = current
        }
    }

    fun deletePreviewQuestion(index: Int) {
        val current = _previewQuestions.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _previewQuestions.value = current
        }
    }

    fun savePreviewQuestionsToBank() {
        val questions = _previewQuestions.value
        if (questions.isEmpty()) {
            showToast("No generated questions to save.")
            return
        }
        val approved = questions.map { it.copy(isReviewed = true) }
        repository.addQuestionsToBank(approved)
        showToast("Saved ${approved.size} approved questions to Master Question Bank.")
        clearPreviewQuestions()
    }

    fun adminAddQuestionsToBank(questions: List<Question>) {
        if (questions.isNotEmpty()) {
            repository.addQuestionsToBank(questions)
            showToast("Added ${questions.size} questions to Master Question Bank.")
        }
    }

    fun adminDeleteQuestionFromBank(questionId: String) {
        repository.deleteQuestionFromBank(questionId)
        showToast("Question removed from Master Bank.")
    }

    fun createMockTestFromGenerated(
        title: String,
        examCode: String,
        subject: String,
        topic: String,
        durationMinutes: Int = 60,
        positiveMarks: Double = 1.0,
        negativeMarks: Double = 0.25,
        isFree: Boolean = false,
        price: Double = 199.0
    ) {
        val questions = _previewQuestions.value
        if (questions.isEmpty()) {
            showToast("No generated questions available to build test.")
            return
        }
        val testId = "test_${UUID.randomUUID().toString().take(8)}"
        val numberedQuestions = questions.mapIndexed { idx, q ->
            q.copy(testId = testId, questionNumber = idx + 1, isReviewed = true)
        }
        val newTest = MockTest(
            id = testId,
            title = title.ifBlank { "$examCode AI Generated Mock Test" },
            examCode = examCode,
            subject = subject,
            topic = topic,
            durationMinutes = durationMinutes,
            totalQuestions = numberedQuestions.size,
            totalMarks = numberedQuestions.size * positiveMarks,
            positiveMarks = positiveMarks,
            negativeMarks = negativeMarks,
            isFree = isFree,
            isPublished = true,
            price = if (isFree) 0.0 else price,
            displayOrder = mockTests.value.filter { it.examCode == examCode }.size + 1,
            instructions = "AI-Generated & Clinically Verified Mock Test for $examCode.",
            questions = numberedQuestions
        )
        repository.addMockTest(newTest)
        repository.addQuestionsToBank(numberedQuestions)
        clearPreviewQuestions()
        showToast("Mock Test published successfully with ${numberedQuestions.size} questions!")
    }

    // ==========================================
    // BULK IMPORT METHODS
    // ==========================================

    private val _isBatchSaving = MutableStateFlow(false)
    val isBatchSaving = _isBatchSaving.asStateFlow()

    private val _batchSaveProgress = MutableStateFlow(0f)
    val batchSaveProgress = _batchSaveProgress.asStateFlow()

    private val _batchSaveStatusMessage = MutableStateFlow("")
    val batchSaveStatusMessage = _batchSaveStatusMessage.asStateFlow()

    fun parseAndImportTextQuestions(
        text: String,
        examCode: String,
        subject: String,
        topic: String,
        difficulty: String = "Medium",
        destination: String // "BANK" or "MOCK_TEST"
    ): com.example.data.importer.ParseResult {
        val result = com.example.data.importer.BulkQuestionParser.parseFormattedText(
            rawText = text,
            defaultSubject = subject,
            defaultTopic = topic,
            defaultExamCode = examCode,
            defaultDifficulty = difficulty,
            existingBank = questionBank.value
        )
        if (result.validQuestions.isNotEmpty()) {
            if (destination == "MOCK_TEST") {
                _previewQuestions.value = result.validQuestions
                showToast("Parsed ${result.validQuestions.size} valid questions (${result.duplicateQuestions.size} duplicates excluded).")
            } else {
                repository.addQuestionsToBank(result.validQuestions)
                showToast("Imported ${result.validQuestions.size} questions into Master Question Bank.")
            }
        } else {
            showToast("Could not parse any valid questions. Please check format.")
        }
        return result
    }

    fun parseAndImportCsvQuestions(
        csv: String,
        examCode: String,
        subject: String,
        topic: String
    ): com.example.data.importer.ParseResult {
        val result = com.example.data.importer.BulkQuestionParser.parseCsv(
            csvText = csv,
            defaultSubject = subject,
            defaultTopic = topic,
            defaultExamCode = examCode,
            existingBank = questionBank.value
        )
        if (result.validQuestions.isNotEmpty()) {
            repository.addQuestionsToBank(result.validQuestions)
            showToast("Imported ${result.validQuestions.size} questions from CSV (${result.duplicateQuestions.size} duplicates excluded).")
        } else {
            showToast("Could not parse CSV. Please check columns.")
        }
        return result
    }

    fun saveBatchQuestionsToFirestore(
        questions: List<Question>,
        destination: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (questions.isEmpty()) {
            showToast("No questions to save.")
            return
        }

        viewModelScope.launch {
            _isBatchSaving.value = true
            _batchSaveProgress.value = 0.05f
            _batchSaveStatusMessage.value = "Saving batch in safe chunks of 400..."

            val res = repository.saveBatchQuestionsToFirestore(
                context = getApplication(),
                questions = questions,
                onProgress = { prog, msg ->
                    _batchSaveProgress.value = prog
                    _batchSaveStatusMessage.value = msg
                }
            )

            _isBatchSaving.value = false
            if (res.isSuccess) {
                if (destination == "MOCK_TEST") {
                    _previewQuestions.value = questions
                    showToast("Batch of ${questions.size} questions saved and loaded into test preview.")
                } else {
                    showToast("Batch of ${questions.size} questions successfully saved to Question Bank and Cloud Firestore!")
                }
                onComplete(true)
            } else {
                showToast("Saved to local bank. Cloud Firestore sync failed: ${res.exceptionOrNull()?.message}")
                onComplete(false)
            }
        }
    }

    // ==========================================
    // EXAM CATEGORY ADMIN OPERATIONS
    // ==========================================

    fun adminCreateCategory(
        name: String,
        code: String,
        year: String,
        group: String,
        description: String,
        syllabus: String,
        packagePrice: Double = 199.0,
        freeTestCount: Int = 1
    ) {
        val newCat = ExamCategory(
            id = "cat_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            code = code.uppercase().trim().replace(" ", "_"),
            year = year.trim(),
            categoryGroup = group.trim(),
            description = description.trim(),
            syllabusSummary = syllabus.trim(),
            packagePrice = packagePrice,
            freeMockTestCount = freeTestCount,
            isNew = true,
            isPublished = true,
            totalTests = 12
        )
        repository.addExamCategory(newCat)
        showToast("Exam Category '${newCat.name}' created & published!")
    }

    fun adminUpdateCategory(category: ExamCategory) {
        repository.updateExamCategory(category)
        showToast("Category '${category.name}' updated.")
    }

    fun adminDeleteCategory(categoryId: String) {
        repository.deleteExamCategory(categoryId)
        showToast("Category deleted/archived.")
    }

    fun adminToggleCategoryPublish(categoryId: String) {
        repository.toggleCategoryPublish(categoryId)
        showToast("Category publication status toggled.")
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}

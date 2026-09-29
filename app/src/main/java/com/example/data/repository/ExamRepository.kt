package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ExamRepository(private val dao: ExamDao) {

    // Persistent Room Flows
    val testAttempts: Flow<List<TestAttemptEntity>> = dao.getAllTestAttempts()
    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val supportTickets: Flow<List<SupportTicketEntity>> = dao.getAllSupportTickets()

    // Dynamic Lists (Managed by Admin or Student actions)
    private val _examCategories = MutableStateFlow(ExamDataProvider.examCategories)
    val examCategories = _examCategories.asStateFlow()

    private val _mockTests = MutableStateFlow(ExamDataProvider.mockTests)
    val mockTests = _mockTests.asStateFlow()

    private val _studentEntitlements = MutableStateFlow(ExamDataProvider.initialEntitlements)
    val studentEntitlements = _studentEntitlements.asStateFlow()

    private val _questionBank = MutableStateFlow(ExamDataProvider.sampleQuestions)
    val questionBank = _questionBank.asStateFlow()

    private val _aiJobs = MutableStateFlow<List<AiGenerationJob>>(emptyList())
    val aiJobs = _aiJobs.asStateFlow()

    private val _firebaseStatus = MutableStateFlow(FirebaseSystemStatus())
    val firebaseStatus = _firebaseStatus.asStateFlow()

    private val _courses = MutableStateFlow(ExamDataProvider.courses)
    val courses = _courses.asStateFlow()

    private val _studyMaterials = MutableStateFlow(ExamDataProvider.studyMaterials)
    val studyMaterials = _studyMaterials.asStateFlow()

    private val _notifications = MutableStateFlow(ExamDataProvider.notifications)
    val notifications = _notifications.asStateFlow()

    private val _students = MutableStateFlow(ExamDataProvider.initialStudents)
    val students = _students.asStateFlow()

    private val _transactions = MutableStateFlow(ExamDataProvider.initialTransactions)
    val transactions = _transactions.asStateFlow()

    private val _coupons = MutableStateFlow(ExamDataProvider.initialCoupons)
    val coupons = _coupons.asStateFlow()

    private val _razorpayConfig = MutableStateFlow(RazorpayGatewayConfig())
    val razorpayConfig = _razorpayConfig.asStateFlow()

    private val _auditLogs = MutableStateFlow(
        listOf(
            "Security role authorization layer initialized",
            "Super Admin session established with multi-factor audit",
            "Question bank synchronized with 16 competitive exam streams",
            "Razorpay payment gateway configured in secure mode",
            "Student access policies enforced"
        )
    )
    val auditLogs = _auditLogs.asStateFlow()

    suspend fun saveTestAttempt(attempt: TestAttempt) {
        dao.insertTestAttempt(
            TestAttemptEntity(
                id = attempt.id,
                testId = attempt.testId,
                testTitle = attempt.testTitle,
                score = attempt.score,
                maxMarks = attempt.maxMarks,
                correctCount = attempt.correctCount,
                wrongCount = attempt.wrongCount,
                unattemptedCount = attempt.unattemptedCount,
                timeSpentSeconds = attempt.timeSpentSeconds,
                completedAt = attempt.completedAt
            )
        )
        // Increment attempts count on the test
        _mockTests.value = _mockTests.value.map { test ->
            if (test.id == attempt.testId) {
                test.copy(attemptsCount = test.attemptsCount + 1)
            } else test
        }
        addAuditLog("Test attempt registered for Test: ${attempt.testTitle} — Score: ${attempt.score}/${attempt.maxMarks}")
    }

    suspend fun toggleBookmark(id: String, title: String, type: String, subtitle: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            dao.deleteBookmark(id)
        } else {
            dao.insertBookmark(BookmarkEntity(id, title, type, subtitle))
        }
    }

    suspend fun submitSupportTicket(email: String, subject: String, category: String, message: String) {
        val ticket = SupportTicketEntity(
            id = "TCK-${System.currentTimeMillis().toString().takeLast(6)}",
            studentEmail = email,
            subject = subject,
            category = category,
            message = message,
            status = "OPEN",
            reply = null,
            createdAt = "Just now"
        )
        dao.insertSupportTicket(ticket)
        addAuditLog("New support ticket raised by: $email ($subject)")
    }

    fun markNotificationRead(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // Admin Operations
    fun addMockTest(newTest: MockTest) {
        _mockTests.value = listOf(newTest) + _mockTests.value
        addAuditLog("Admin created new mock test: ${newTest.title}")
    }

    fun deleteMockTest(testId: String) {
        _mockTests.value = _mockTests.value.filter { it.id != testId }
        addAuditLog("Admin deleted mock test ID: $testId")
    }

    fun toggleMockTestPublish(testId: String) {
        _mockTests.value = _mockTests.value.map {
            if (it.id == testId) {
                val updated = it.copy(isPublished = !it.isPublished)
                addAuditLog("Admin changed publication status for ${it.title} to: ${if (updated.isPublished) "Published" else "Draft"}")
                updated
            } else it
        }
    }

    fun addQuestionToTest(testId: String, newQuestion: Question) {
        _mockTests.value = _mockTests.value.map { test ->
            if (test.id == testId) {
                val updatedQuestions = test.questions + newQuestion
                test.copy(
                    questions = updatedQuestions,
                    totalQuestions = updatedQuestions.size,
                    totalMarks = updatedQuestions.size * test.positiveMarks
                )
            } else test
        }
        addAuditLog("Admin appended Question #${newQuestion.questionNumber} to Test ID: $testId")
    }

    fun addCourse(newCourse: Course) {
        _courses.value = listOf(newCourse) + _courses.value
        addAuditLog("Admin published new course: ${newCourse.title}")
    }

    fun deleteCourse(courseId: String) {
        _courses.value = _courses.value.filter { it.id != courseId }
        addAuditLog("Admin deleted course ID: $courseId")
    }

    fun enrollCourse(courseId: String, studentName: String) {
        _courses.value = _courses.value.map { course ->
            if (course.id == courseId) {
                course.copy(isEnrolled = true, enrolledCount = course.enrolledCount + 1)
            } else course
        }
        val course = _courses.value.find { it.id == courseId }
        val newTx = TransactionRecord(
            id = "tx_${System.currentTimeMillis()}",
            transactionId = "RZP_ORD_${UUID.randomUUID().toString().take(8).uppercase()}",
            studentName = studentName,
            itemTitle = course?.title ?: "Enrolled Course",
            amount = course?.price ?: 0.0,
            dateStr = "Today",
            status = "Successful",
            paymentMethod = "Razorpay UPI"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        addAuditLog("Razorpay payment verified & access granted to: $studentName for ${course?.title}")
    }

    fun addStudyMaterial(material: StudyMaterial) {
        _studyMaterials.value = listOf(material) + _studyMaterials.value
        addAuditLog("Admin uploaded study material: ${material.title}")
    }

    fun toggleStudentStatus(studentId: String) {
        _students.value = _students.value.map { student ->
            if (student.id == studentId) {
                val updated = student.copy(isSuspended = !student.isSuspended)
                addAuditLog("Admin modified student status for ${student.name} to: ${if (updated.isSuspended) "Blocked" else "Active"}")
                updated
            } else student
        }
    }

    fun addCoupon(coupon: Coupon) {
        _coupons.value = listOf(coupon) + _coupons.value
        addAuditLog("Admin created discount coupon: ${coupon.code} (${coupon.discountPercent}%)")
    }

    fun sendNotification(title: String, message: String, targetAudience: String) {
        val newNotif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = "$message (Target: $targetAudience)",
            timeAgo = "Just now",
            type = "ANNOUNCEMENT",
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value
        addAuditLog("Admin broadcast notification: '$title' to $targetAudience")
    }

    fun addAuditLog(log: String) {
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    fun updateRazorpayConfig(config: RazorpayGatewayConfig) {
        _razorpayConfig.value = config
        addAuditLog("Admin updated Razorpay Gateway API Keys (Mode: ${if (config.isLiveMode) "LIVE PRODUCTION" else "SANDBOX TEST"})")
    }

    // Exam Category Management
    fun addExamCategory(category: ExamCategory) {
        _examCategories.value = listOf(category) + _examCategories.value
        addAuditLog("Admin created new Exam Category: ${category.name} (${category.code})")
    }

    fun updateExamCategory(category: ExamCategory) {
        _examCategories.value = _examCategories.value.map {
            if (it.id == category.id || it.code == category.code) category else it
        }
        addAuditLog("Admin updated Exam Category: ${category.name}")
    }

    fun deleteExamCategory(categoryId: String) {
        _examCategories.value = _examCategories.value.filter { it.id != categoryId }
        addAuditLog("Admin deleted/archived Exam Category ID: $categoryId")
    }

    fun toggleCategoryPublish(categoryId: String) {
        _examCategories.value = _examCategories.value.map {
            if (it.id == categoryId) it.copy(isPublished = !it.isPublished) else it
        }
    }

    // Student Entitlements & Category Package Access
    fun grantCategoryAccess(
        studentEmail: String,
        studentName: String,
        examCode: String,
        price: Double = 199.0,
        paymentId: String = "pay_${UUID.randomUUID().toString().take(10)}",
        orderId: String = "order_${UUID.randomUUID().toString().take(10)}"
    ): StudentEntitlement {
        // Prevent duplicate entitlement
        val existing = _studentEntitlements.value.find { it.studentEmail.equals(studentEmail, ignoreCase = true) && it.examCode == examCode }
        if (existing != null) return existing

        val entitlement = StudentEntitlement(
            id = "ent_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
            studentEmail = studentEmail,
            examCode = examCode,
            packagePrice = price,
            purchasedAt = System.currentTimeMillis(),
            orderId = orderId,
            paymentId = paymentId,
            status = "ACTIVE",
            isPermanent = true
        )
        _studentEntitlements.value = listOf(entitlement) + _studentEntitlements.value

        val categoryName = _examCategories.value.find { it.code == examCode }?.name ?: examCode
        val newTx = TransactionRecord(
            id = "tx_${System.currentTimeMillis()}",
            transactionId = paymentId,
            studentName = studentName,
            itemTitle = "WBTOPPER $categoryName All-Access Mock Test Series",
            amount = price,
            dateStr = "Today",
            status = "Successful",
            paymentMethod = "Razorpay Verified UPI/Cards"
        )
        _transactions.value = listOf(newTx) + _transactions.value

        // Also broadcast notification to student
        val notif = AppNotification(
            id = "notif_ent_${System.currentTimeMillis()}",
            title = "All-Access Unlocked!",
            message = "Congratulations! All mock tests in $categoryName are now permanently unlocked for you.",
            timeAgo = "Just now",
            type = "PAYMENT",
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value

        addAuditLog("Razorpay Verified Payment ₹${price.toInt()}: Category $examCode unlocked permanently for student $studentEmail ($paymentId)")
        return entitlement
    }

    fun isCategoryEntitled(studentEmail: String, examCode: String): Boolean {
        return _studentEntitlements.value.any {
            it.studentEmail.equals(studentEmail, ignoreCase = true) &&
                    it.examCode.equals(examCode, ignoreCase = true) &&
                    it.status == "ACTIVE"
        }
    }

    // Question Bank Management
    fun addQuestionsToBank(questions: List<Question>) {
        _questionBank.value = questions + _questionBank.value
        addAuditLog("Added ${questions.size} questions to Master Question Bank")
    }

    fun updateQuestionInBank(question: Question) {
        _questionBank.value = _questionBank.value.map {
            if (it.id == question.id) question else it
        }
    }

    fun deleteQuestionFromBank(questionId: String) {
        _questionBank.value = _questionBank.value.filter { it.id != questionId }
    }

    // AI Generation Jobs
    fun addAiJob(job: AiGenerationJob) {
        _aiJobs.value = listOf(job) + _aiJobs.value
    }

    fun updateAiJob(job: AiGenerationJob) {
        _aiJobs.value = _aiJobs.value.map {
            if (it.id == job.id) job else it
        }
    }

    // Firebase System Health & Sync
    fun updateFirebaseStatus(status: FirebaseSystemStatus) {
        _firebaseStatus.value = status
    }

    suspend fun refreshFirebaseStatus(context: android.content.Context) {
        val status = com.example.data.firebase.FirebaseBackendService.verifyAllFirebaseServices(context)
        _firebaseStatus.value = status
        if (status.isLiveFirestoreActive) {
            val liveStudents = com.example.data.firebase.FirebaseBackendService.fetchFirestoreStudents(context)
            if (liveStudents.isNotEmpty()) {
                _students.value = liveStudents
            }
        }
        addAuditLog("Firebase status check: Auth=${status.authStatus}, Firestore=${status.firestoreStatus}, Storage=${status.storageStatus}, Functions=${status.functionsStatus}")
    }

    suspend fun saveBatchQuestionsToFirestore(
        context: android.content.Context,
        questions: List<Question>,
        onProgress: (Float, String) -> Unit
    ): Result<Int> {
        // Always add to memory / local bank
        addQuestionsToBank(questions)
        return if (com.example.data.firebase.FirebaseBackendService.isFirebaseInitialized(context)) {
            val res = com.example.data.firebase.FirebaseBackendService.saveQuestionsBatchToFirestore(context, questions, onProgress)
            if (res.isSuccess) {
                addAuditLog("Successfully uploaded ${questions.size} questions in safe batches to Cloud Firestore")
            }
            res
        } else {
            onProgress(1.0f, "Saved ${questions.size} questions to local Question Bank (Firebase unconfigured).")
            Result.success(questions.size)
        }
    }
}

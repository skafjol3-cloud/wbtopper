package com.example.data.model

enum class UserRole {
    STUDENT,
    ADMIN
}

data class ExamCategory(
    val id: String,
    val name: String,
    val year: String = "2026",
    val code: String,
    val subtitle: String = "",
    val isNew: Boolean = false,
    val isPremium: Boolean = false,
    val totalTests: Int = 24,
    val totalCandidates: String = "15.4K+",
    val description: String = "",
    val syllabusSummary: String = "",
    val categoryGroup: String = "Nursing & Paramedical",
    val packagePrice: Double = 199.0,
    val freeMockTestCount: Int = 1,
    val isPublished: Boolean = true,
    val displayOrder: Int = 0,
    val packageDescription: String = "WBTOPPER Exam Mock Test All-Access - One-time ₹199 unlocks all remaining tests in this series permanently.",
    val packageInclusions: List<String> = listOf(
        "1 Free Mock Test (Permanent Free Access)",
        "All remaining Full-Length Mock Tests unlocked",
        "Step-by-step clinical explanations & answer keys",
        "Real-time state & national ranking percentile",
        "Permanent validity - No recurring fees"
    )
)

data class Question(
    val id: String,
    val testId: String = "",
    val questionNumber: Int,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val subject: String,
    val topic: String = "Core Concepts",
    val difficulty: String = "Medium",
    val year: String = "2026",
    val isAiGenerated: Boolean = false,
    val isReviewed: Boolean = true,
    val source: String = "Official Syllabus",
    val marks: Double = 1.0,
    val negativeMarks: Double = 0.25,
    val examCode: String = "WBHRB_SN"
)

data class MockTest(
    val id: String,
    val title: String,
    val examCode: String,
    val subject: String = "Complete Syllabus",
    val topic: String = "Full Length Mock",
    val durationMinutes: Int = 60,
    val totalQuestions: Int = 50,
    val totalMarks: Double = 50.0,
    val positiveMarks: Double = 1.0,
    val negativeMarks: Double = 0.25,
    val isFree: Boolean = true,
    val isPublished: Boolean = true,
    val price: Double = 0.0,
    val attemptsCount: Int = 1420,
    val displayOrder: Int = 0,
    val instructions: String = "Each question carries positive marks as specified. Negative marking applies for wrong answers. No deduction for unattempted questions.",
    val questions: List<Question> = emptyList()
)

data class StudentEntitlement(
    val id: String,
    val studentEmail: String,
    val examCode: String,
    val packagePrice: Double = 199.0,
    val purchasedAt: Long = System.currentTimeMillis(),
    val orderId: String,
    val paymentId: String,
    val status: String = "ACTIVE",
    val isPermanent: Boolean = true
)

data class AiGenerationJob(
    val id: String,
    val examCode: String,
    val subject: String,
    val topic: String,
    val requestedCount: Int,
    val difficulty: String,
    val status: String = "COMPLETED", // IN_PROGRESS, COMPLETED, FAILED
    val progress: Float = 1.0f,
    val questions: List<Question> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class ServiceConnectionState {
    CONNECTED,
    NOT_CONFIGURED,
    ERROR,
    UNVERIFIED,
    DEPLOYED,
    NOT_DEPLOYED
}

data class FirebaseFileItem(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val contentType: String,
    val downloadUrl: String = "",
    val updatedTimeStr: String = "Recent"
)

data class FirebaseSystemStatus(
    val isInitialized: Boolean = false,
    val projectId: String = "Not Configured (Requires google-services.json)",
    val region: String = "asia-south1 (Mumbai)",
    val authStatus: ServiceConnectionState = ServiceConnectionState.NOT_CONFIGURED,
    val authStatusMessage: String = "Firebase Auth not initialized. Place google-services.json in /app to connect.",
    val firestoreStatus: ServiceConnectionState = ServiceConnectionState.NOT_CONFIGURED,
    val firestoreStatusMessage: String = "Firestore not initialized. Operating in local Room offline mode.",
    val storageStatus: ServiceConnectionState = ServiceConnectionState.NOT_CONFIGURED,
    val storageStatusMessage: String = "Storage bucket not initialized. Place google-services.json to enable cloud uploads.",
    val functionsStatus: ServiceConnectionState = ServiceConnectionState.NOT_DEPLOYED,
    val functionsStatusMessage: String = "Cloud Functions not deployed or unverified. Never claimed deployed without check.",
    val functionsVersion: String = "Unverified",
    val liveUsersCount: Int = 0,
    val liveCategoriesCount: Int = 0,
    val liveMockTestsCount: Int = 0,
    val liveQuestionsCount: Int = 0,
    val liveOrdersCount: Int = 0,
    val storageUsedMb: Double = 0.0,
    val storageFiles: List<FirebaseFileItem> = emptyList(),
    val lastSyncTime: String = "Awaiting configuration",
    val isLiveFirestoreActive: Boolean = false
) {
    val isAuthConnected: Boolean get() = authStatus == ServiceConnectionState.CONNECTED
    val isFirestoreConnected: Boolean get() = firestoreStatus == ServiceConnectionState.CONNECTED
    val isStorageConnected: Boolean get() = storageStatus == ServiceConnectionState.CONNECTED
    val isCloudFunctionsHealthy: Boolean get() = functionsStatus == ServiceConnectionState.DEPLOYED
    val totalUsersCount: Int get() = liveUsersCount
    val activeCategoriesCount: Int get() = liveCategoriesCount
    val totalMockTestsCount: Int get() = liveMockTestsCount
    val totalQuestionsCount: Int get() = liveQuestionsCount
}

data class TestAttempt(
    val id: String,
    val testId: String,
    val testTitle: String,
    val score: Double,
    val maxMarks: Double,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val timeSpentSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val answersMap: Map<Int, Int> = emptyMap() // questionNumber to selectedOption
)

data class Course(
    val id: String,
    val title: String,
    val examCode: String,
    val description: String,
    val instructor: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Double = 4.8,
    val enrolledCount: Int = 890,
    val totalLessons: Int = 28,
    val totalMockTests: Int = 12,
    val isEnrolled: Boolean = false,
    val isFeatured: Boolean = true,
    val isPremium: Boolean = true,
    val lessons: List<Lesson> = emptyList()
)

data class Lesson(
    val id: String,
    val courseId: String,
    val chapterTitle: String,
    val title: String,
    val duration: String,
    val videoUrl: String,
    val isDemo: Boolean = false,
    val pdfNotesTitle: String = ""
)

data class StudyMaterial(
    val id: String,
    val title: String,
    val examCode: String,
    val subject: String,
    val category: String = "PDF", // Notes, PDF, PYQ, Short Notes, Revision Notes, Practice Sets
    val fileType: String = "PDF",
    val pageCount: Int = 45,
    val fileSize: String = "4.2 MB",
    val isFree: Boolean = true,
    val downloadCount: Int = 1250,
    val description: String = ""
)

data class LiveClass(
    val id: String,
    val title: String,
    val examCode: String,
    val dateStr: String,
    val timeStr: String,
    val meetingUrl: String,
    val status: String = "UPCOMING",
    val instructorName: String = "Senior Faculty"
)

data class StudentUser(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val studentId: String = "BEP-2026-891",
    val targetExam: String,
    val role: UserRole = UserRole.STUDENT,
    val isSuspended: Boolean = false,
    val enrolledCoursesCount: Int = 1,
    val testAttemptsCount: Int = 5,
    val joinedDate: String = "2026-02-14",
    val lastActive: String = "Today",
    val isEmailVerified: Boolean = true,
    val authProvider: String = "Firebase Auth / Password"
)

data class TransactionRecord(
    val id: String,
    val transactionId: String,
    val studentName: String,
    val itemTitle: String,
    val amount: Double,
    val dateStr: String,
    val status: String = "Successful", // Successful, Pending, Failed, Refunded
    val paymentMethod: String = "Razorpay UPI"
)

data class Coupon(
    val code: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minPurchase: Double = 199.0,
    val expiryDate: String,
    val usageCount: Int = 0,
    val usageLimit: Int = 500,
    val applicableCourse: String = "All Courses",
    val isActive: Boolean = true
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String = "EXAM_UPDATE", // NEW_TEST, NEW_COURSE, MATERIAL, EXAM_UPDATE, ANNOUNCEMENT, PAYMENT
    val isRead: Boolean = false
)

data class SupportTicket(
    val id: String,
    val studentEmail: String,
    val subject: String,
    val category: String,
    val message: String,
    val status: String = "OPEN",
    val reply: String? = null,
    val createdAt: String = "Today"
)

data class RazorpayGatewayConfig(
    val keyId: String = "rzp_test_1DP5mmOlF5G5ag",
    val keySecret: String = "s6Z89Qk4yVwP123XyZabcdEf",
    val webhookSecret: String = "whsec_bep_2026_razorpay_secret",
    val merchantName: String = "WBTOPPER",
    val isLiveMode: Boolean = false,
    val autoCapture: Boolean = true,
    val currency: String = "INR",
    val isConfigured: Boolean = true,
    val lastUpdated: String = "Today"
)

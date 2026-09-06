package com.example.data.model

enum class UserRole {
    CLIENT,
    WORKER
}

enum class VerificationStatus {
    UNSUBMITTED,
    PENDING,
    VERIFIED,
    REJECTED
}

enum class JobStatus {
    PENDING,
    ACCEPTED,
    WORKER_COMPLETED,
    COMPLETED,
    DISPUTED
}

enum class EscrowStatus {
    NONE,
    PENDING,
    HELD,
    RELEASED,
    REFUNDED
}

enum class ServiceRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    WORKER_COMPLETED,
    COMPLETED
}

enum class UrgencyLevel {
    STANDARD,
    EMERGENCY
}

data class User(
    val id: Long = 1L,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val isClient: Boolean,
    val isWorker: Boolean,
    val activeRole: UserRole = if (isClient) UserRole.CLIENT else UserRole.WORKER,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val rejectionReason: String? = null,
    val walletBalance: Double = 1250.0,
    val bankName: String = "State Bank of India",
    val accountNumber: String = "XXXX-XXXX-4819",
    val ifscCode: String = "SBIN0001234",
    val rating: Float = 4.8f,
    val jobsCompleted: Int = 14
)

data class Job(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val serviceType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val budget: Double,
    val isNegotiable: Boolean = true,
    val urgencyLevel: UrgencyLevel = UrgencyLevel.STANDARD,
    val status: JobStatus = JobStatus.PENDING,
    val escrowStatus: EscrowStatus = EscrowStatus.NONE,
    val clientId: Long,
    val clientName: String,
    val workerId: Long? = null,
    val workerName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val distanceKm: Double = 2.4
)

data class TradeCategory(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val iconKey: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val baseRate: Int,
    val isPopular: Boolean = false
) {
    val title: String get() = nameEn
    val hindiTitle: String get() = nameHi
    val baseHourlyRate: Double get() = baseRate.toDouble()
}

data class TradeProfile(
    val id: Long = 0L,
    val workerId: Long,
    val workerName: String,
    val displayName: String,
    val tradeCategory: String,
    val skills: String,
    val experienceDescription: String,
    val yearsOfExperience: Int,
    val availability: String,
    val toolsEquipment: String,
    val languages: String,
    val rating: Float = 4.8f,
    val reviewCount: Int = 18,
    val isActive: Boolean = true,
    val hourlyRate: Double = 450.0
) {
    val experienceDesc: String get() = experienceDescription
    val tools: String get() = toolsEquipment
}

data class ServiceRequest(
    val id: Long = 0L,
    val clientId: Long,
    val clientName: String,
    val workerId: Long,
    val workerName: String,
    val tradeProfileId: Long,
    val tradeCategory: String,
    val date: String,
    val timeSlot: String,
    val description: String,
    val budget: Double,
    val status: ServiceRequestStatus = ServiceRequestStatus.PENDING,
    val escrowStatus: EscrowStatus = EscrowStatus.NONE,
    val workerNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class Notification(
    val id: Long = 0L,
    val title: String,
    val message: String,
    val type: String, // "job_accepted", "payment_required", "escrow_funded", "job_completed", "escrow_released", "dispute"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedJobId: Long? = null
)

data class Review(
    val id: Long = 0L,
    val workerId: Long,
    val workerName: String,
    val clientName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val serviceType: String
)

data class KycDocument(
    val idType: String, // "Aadhaar", "PAN", "Passport", "Driving Licence"
    val frontUploaded: Boolean = false,
    val backUploaded: Boolean = false,
    val selfieUploaded: Boolean = false,
    val submittedAt: Long? = null
)

typealias RequestStatus = ServiceRequestStatus


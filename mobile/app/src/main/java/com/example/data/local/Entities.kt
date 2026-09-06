package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EscrowStatus
import com.example.data.model.Job
import com.example.data.model.JobStatus
import com.example.data.model.Notification
import com.example.data.model.Review
import com.example.data.model.ServiceRequest
import com.example.data.model.ServiceRequestStatus
import com.example.data.model.TradeProfile
import com.example.data.model.UrgencyLevel
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val isClient: Boolean,
    val isWorker: Boolean,
    val activeRole: String, // "CLIENT", "WORKER"
    val verificationStatus: String, // "UNSUBMITTED", "PENDING", "VERIFIED", "REJECTED"
    val rejectionReason: String?,
    val walletBalance: Double,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val rating: Float,
    val jobsCompleted: Int
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        isClient = isClient,
        isWorker = isWorker,
        activeRole = runCatching { UserRole.valueOf(activeRole) }.getOrDefault(UserRole.CLIENT),
        verificationStatus = runCatching { VerificationStatus.valueOf(verificationStatus) }.getOrDefault(VerificationStatus.VERIFIED),
        rejectionReason = rejectionReason,
        walletBalance = walletBalance,
        bankName = bankName,
        accountNumber = accountNumber,
        ifscCode = ifscCode,
        rating = rating,
        jobsCompleted = jobsCompleted
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            phoneNumber = user.phoneNumber,
            isClient = user.isClient,
            isWorker = user.isWorker,
            activeRole = user.activeRole.name,
            verificationStatus = user.verificationStatus.name,
            rejectionReason = user.rejectionReason,
            walletBalance = user.walletBalance,
            bankName = user.bankName,
            accountNumber = user.accountNumber,
            ifscCode = user.ifscCode,
            rating = user.rating,
            jobsCompleted = user.jobsCompleted
        )
    }
}

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String,
    val serviceType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val budget: Double,
    val isNegotiable: Boolean,
    val urgencyLevel: String, // "STANDARD", "EMERGENCY"
    val status: String, // "PENDING", "ACCEPTED", "WORKER_COMPLETED", "COMPLETED", "DISPUTED"
    val escrowStatus: String, // "NONE", "PENDING", "HELD", "RELEASED", "REFUNDED"
    val clientId: Long,
    val clientName: String,
    val workerId: Long?,
    val workerName: String?,
    val createdAt: Long,
    val distanceKm: Double
) {
    fun toDomain(): Job = Job(
        id = id,
        title = title,
        description = description,
        serviceType = serviceType,
        latitude = latitude,
        longitude = longitude,
        address = address,
        budget = budget,
        isNegotiable = isNegotiable,
        urgencyLevel = runCatching { UrgencyLevel.valueOf(urgencyLevel) }.getOrDefault(UrgencyLevel.STANDARD),
        status = runCatching { JobStatus.valueOf(status) }.getOrDefault(JobStatus.PENDING),
        escrowStatus = runCatching { EscrowStatus.valueOf(escrowStatus) }.getOrDefault(EscrowStatus.NONE),
        clientId = clientId,
        clientName = clientName,
        workerId = workerId,
        workerName = workerName,
        createdAt = createdAt,
        distanceKm = distanceKm
    )

    companion object {
        fun fromDomain(job: Job): JobEntity = JobEntity(
            id = job.id,
            title = job.title,
            description = job.description,
            serviceType = job.serviceType,
            latitude = job.latitude,
            longitude = job.longitude,
            address = job.address,
            budget = job.budget,
            isNegotiable = job.isNegotiable,
            urgencyLevel = job.urgencyLevel.name,
            status = job.status.name,
            escrowStatus = job.escrowStatus.name,
            clientId = job.clientId,
            clientName = job.clientName,
            workerId = job.workerId,
            workerName = job.workerName,
            createdAt = job.createdAt,
            distanceKm = job.distanceKm
        )
    }
}

@Entity(tableName = "trade_profiles")
data class TradeProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
    val rating: Float,
    val reviewCount: Int,
    val isActive: Boolean,
    val hourlyRate: Double
) {
    fun toDomain(): TradeProfile = TradeProfile(
        id = id,
        workerId = workerId,
        workerName = workerName,
        displayName = displayName,
        tradeCategory = tradeCategory,
        skills = skills,
        experienceDescription = experienceDescription,
        yearsOfExperience = yearsOfExperience,
        availability = availability,
        toolsEquipment = toolsEquipment,
        languages = languages,
        rating = rating,
        reviewCount = reviewCount,
        isActive = isActive,
        hourlyRate = hourlyRate
    )

    companion object {
        fun fromDomain(profile: TradeProfile): TradeProfileEntity = TradeProfileEntity(
            id = profile.id,
            workerId = profile.workerId,
            workerName = profile.workerName,
            displayName = profile.displayName,
            tradeCategory = profile.tradeCategory,
            skills = profile.skills,
            experienceDescription = profile.experienceDescription,
            yearsOfExperience = profile.yearsOfExperience,
            availability = profile.availability,
            toolsEquipment = profile.toolsEquipment,
            languages = profile.languages,
            rating = profile.rating,
            reviewCount = profile.reviewCount,
            isActive = profile.isActive,
            hourlyRate = profile.hourlyRate
        )
    }
}

@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
    val status: String,
    val escrowStatus: String,
    val workerNotes: String?,
    val createdAt: Long
) {
    fun toDomain(): ServiceRequest = ServiceRequest(
        id = id,
        clientId = clientId,
        clientName = clientName,
        workerId = workerId,
        workerName = workerName,
        tradeProfileId = tradeProfileId,
        tradeCategory = tradeCategory,
        date = date,
        timeSlot = timeSlot,
        description = description,
        budget = budget,
        status = runCatching { ServiceRequestStatus.valueOf(status) }.getOrDefault(ServiceRequestStatus.PENDING),
        escrowStatus = runCatching { EscrowStatus.valueOf(escrowStatus) }.getOrDefault(EscrowStatus.NONE),
        workerNotes = workerNotes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(req: ServiceRequest): ServiceRequestEntity = ServiceRequestEntity(
            id = req.id,
            clientId = req.clientId,
            clientName = req.clientName,
            workerId = req.workerId,
            workerName = req.workerName,
            tradeProfileId = req.tradeProfileId,
            tradeCategory = req.tradeCategory,
            date = req.date,
            timeSlot = req.timeSlot,
            description = req.description,
            budget = req.budget,
            status = req.status.name,
            escrowStatus = req.escrowStatus.name,
            workerNotes = req.workerNotes,
            createdAt = req.createdAt
        )
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val timestamp: Long,
    val relatedJobId: Long? = null
) {
    fun toDomain(): Notification = Notification(
        id = id,
        title = title,
        message = message,
        type = type,
        isRead = isRead,
        timestamp = timestamp,
        relatedJobId = relatedJobId
    )

    companion object {
        fun fromDomain(n: Notification): NotificationEntity = NotificationEntity(
            id = n.id,
            title = n.title,
            message = n.message,
            type = n.type,
            isRead = n.isRead,
            timestamp = n.timestamp,
            relatedJobId = n.relatedJobId
        )
    }
}

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workerId: Long,
    val workerName: String,
    val clientName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val serviceType: String
) {
    fun toDomain(): Review = Review(
        id = id,
        workerId = workerId,
        workerName = workerName,
        clientName = clientName,
        rating = rating,
        comment = comment,
        date = date,
        serviceType = serviceType
    )

    companion object {
        fun fromDomain(r: Review): ReviewEntity = ReviewEntity(
            id = r.id,
            workerId = r.workerId,
            workerName = r.workerName,
            clientName = r.clientName,
            rating = r.rating,
            comment = r.comment,
            date = r.date,
            serviceType = r.serviceType
        )
    }
}

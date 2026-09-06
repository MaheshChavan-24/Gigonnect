package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.JobEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.ServiceRequestEntity
import com.example.data.local.TradeProfileEntity
import com.example.data.local.UserEntity
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
import com.example.data.network.ApiClient
import com.example.data.network.ApiService
import com.example.data.network.SessionManager
import com.example.data.network.dto.CreateJobRequest
import com.example.data.network.dto.CreateReviewRequest
import com.example.data.network.dto.CreateServiceRequestDto
import com.example.data.network.dto.CreateTradeProfileRequest
import com.example.data.network.dto.JobDto
import com.example.data.network.dto.LoginRequest
import com.example.data.network.dto.NotificationDto
import com.example.data.network.dto.PayJobResponse
import com.example.data.network.dto.PayoutRequest
import com.example.data.network.dto.RegisterRequest
import com.example.data.network.dto.RespondServiceRequestDto
import com.example.data.network.dto.ReviewDto
import com.example.data.network.dto.ServiceRequestDto
import com.example.data.network.dto.TradeProfileDto
import com.example.data.network.dto.UserDto
import com.example.data.network.dto.VerifyPaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SahayaRepository(
    private val database: AppDatabase,
    private val sessionManager: SessionManager
) {
    private val userDao = database.userDao()
    private val jobDao = database.jobDao()
    private val tradeProfileDao = database.tradeProfileDao()
    private val serviceRequestDao = database.serviceRequestDao()
    private val notificationDao = database.notificationDao()
    private val reviewDao = database.reviewDao()

    private val api: ApiService
        get() = ApiClient.getService(sessionManager)

    // Flow observations from Room (cached data synced from backend)
    val currentUser: Flow<User?> = userDao.getUserFlow(sessionManager.getUserId()).map { it?.toDomain() }

    val availableJobs: Flow<List<Job>> = jobDao.getAvailableJobs().map { list -> list.map { it.toDomain() } }

    fun getClientJobs(clientId: Long): Flow<List<Job>> =
        jobDao.getClientJobs(clientId).map { list -> list.map { it.toDomain() } }

    fun getWorkerActiveJob(workerId: Long): Flow<Job?> =
        jobDao.getWorkerActiveJob(workerId).map { it?.toDomain() }

    fun getWorkerHistory(workerId: Long): Flow<List<Job>> =
        jobDao.getWorkerHistory(workerId).map { list -> list.map { it.toDomain() } }

    fun getJobById(jobId: Long): Flow<Job?> =
        jobDao.getJobById(jobId).map { it?.toDomain() }

    val allTradeProfiles: Flow<List<TradeProfile>> =
        tradeProfileDao.getAllActiveProfiles().map { list -> list.map { it.toDomain() } }

    fun getProfilesByCategory(category: String): Flow<List<TradeProfile>> =
        tradeProfileDao.getProfilesByCategory(category).map { list -> list.map { it.toDomain() } }

    fun getMyTradeProfiles(workerId: Long): Flow<List<TradeProfile>> =
        tradeProfileDao.getMyTradeProfiles(workerId).map { list -> list.map { it.toDomain() } }

    fun getClientRequests(clientId: Long): Flow<List<ServiceRequest>> =
        serviceRequestDao.getClientRequests(clientId).map { list -> list.map { it.toDomain() } }

    fun getWorkerRequests(workerId: Long): Flow<List<ServiceRequest>> =
        serviceRequestDao.getWorkerRequests(workerId).map { list -> list.map { it.toDomain() } }

    val allNotifications: Flow<List<Notification>> =
        notificationDao.getAllNotifications().map { list -> list.map { it.toDomain() } }

    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    fun getWorkerReviews(workerId: Long): Flow<List<Review>> =
        reviewDao.getReviewsForWorker(workerId).map { list -> list.map { it.toDomain() } }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getActiveRole(): UserRole = sessionManager.getActiveRole()

    // ==========================================
    // AUTH & PROFILE NETWORK CALLS
    // ==========================================

    suspend fun login(username: String, password: String):Result<User> {
        return try {
            val response = api.login(LoginRequest(username = username, password = password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveTokens(body.access, body.refresh)

                // Fetch full profile from /api/users/me/
                val meResponse = api.getCurrentUser()
                val userDto = if (meResponse.isSuccessful && meResponse.body() != null) {
                    meResponse.body()!!
                } else {
                    body.user ?: UserDto(id = 1L, username = username)
                }

                val role = if (userDto.isWorker) UserRole.WORKER else UserRole.CLIENT
                sessionManager.saveUserSession(userDto.id, userDto.username, userDto.email, role)

                val user = userDto.toDomain(role)
                userDao.insertUser(UserEntity.fromDomain(user))
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed. Check your credentials."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        phoneNumber: String,
        isClient: Boolean,
        isWorker: Boolean
    ): Result<String> {
        return try {
            val response = api.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    phoneNumber = phoneNumber,
                    isClient = isClient,
                    isWorker = isWorker
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                val error = response.errorBody()?.string() ?: "Registration failed."
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val role = sessionManager.getActiveRole()
                sessionManager.saveUserSession(dto.id, dto.username, dto.email, role)
                val user = dto.toDomain(role)
                userDao.insertUser(UserEntity.fromDomain(user))
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to fetch user profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun switchUserRole(newRole: UserRole) {
        sessionManager.setActiveRole(newRole)
        val current = userDao.getUser(sessionManager.getUserId())
        if (current != null) {
            userDao.updateUser(current.copy(activeRole = newRole.name))
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    // ==========================================
    // JOB MARKETPLACE NETWORK CALLS
    // ==========================================

    suspend fun fetchAvailableJobs(lat: Double = 18.5204, lon: Double = 73.8567): Result<List<Job>> {
        return try {
            val response = api.getAvailableJobs(lat = lat, lon = lon)
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!
                val entities = dtoList.map { it.toEntity() }
                jobDao.insertAll(entities)
                Result.success(dtoList.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load jobs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postJob(
        title: String,
        description: String,
        serviceType: String,
        latitude: Double,
        longitude: Double,
        address: String,
        budget: Double,
        isNegotiable: Boolean,
        urgencyLevel: UrgencyLevel,
        clientId: Long,
        clientName: String
    ): Result<Job> {
        return try {
            val request = CreateJobRequest(
                title = title,
                description = description,
                serviceType = serviceType,
                latitude = latitude,
                longitude = longitude,
                address = address,
                budget = budget,
                isNegotiable = isNegotiable,
                urgencyLevel = if (urgencyLevel == UrgencyLevel.EMERGENCY) "Emergency" else "Standard"
            )
            val response = api.createJob(request)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                jobDao.insertJob(dto.toEntity())
                refreshNotifications()
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to post job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptJob(jobId: Long, workerId: Long, workerName: String): Result<Boolean> {
        return try {
            val response = api.acceptJob(jobId)
            if (response.isSuccessful) {
                fetchWorkerActiveJob()
                refreshNotifications()
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to accept job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchClientJobs(): Result<List<Job>> {
        return try {
            val response = api.getClientJobs()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                jobDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch your jobs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchWorkerActiveJob(): Result<Job?> {
        return try {
            val response = api.getActiveJob()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                jobDao.insertJob(dto.toEntity())
                Result.success(dto.toDomain())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchWorkerHistory(): Result<List<Job>> {
        return try {
            val response = api.getWorkerHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                jobDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch worker history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun workerMarkJobComplete(jobId: Long): Result<Boolean> {
        return try {
            val response = api.workerCompleteJob(jobId)
            if (response.isSuccessful) {
                fetchWorkerActiveJob()
                refreshNotifications()
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to mark complete"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clientApproveAndRelease(jobId: Long): Result<Boolean> {
        return try {
            val response = api.completeJob(jobId)
            if (response.isSuccessful) {
                fetchClientJobs()
                refreshCurrentUser()
                refreshNotifications()
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to release escrow"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clientDisputeJob(jobId: Long, reason: String): Result<Boolean> {
        return try {
            val response = api.disputeJob(jobId)
            if (response.isSuccessful) {
                fetchClientJobs()
                refreshNotifications()
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to dispute job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fundJobEscrow(jobId: Long): Result<Boolean> {
        return try {
            val payResponse = api.payJob(jobId)
            if (payResponse.isSuccessful && payResponse.body() != null) {
                val payData = payResponse.body()!!
                val orderId = payData.razorpayOrderId ?: "order_${System.currentTimeMillis()}"

                // Verify payment with simulated signature
                val verifyResponse = api.verifyPayment(
                    id = jobId,
                    request = VerifyPaymentRequest(
                        razorpayOrderId = orderId,
                        razorpayPaymentId = "pay_${System.currentTimeMillis()}",
                        razorpaySignature = "simulated_valid_signature"
                    )
                )
                if (verifyResponse.isSuccessful) {
                    fetchClientJobs()
                    refreshNotifications()
                    Result.success(true)
                } else {
                    Result.failure(Exception(verifyResponse.errorBody()?.string() ?: "Payment verification failed"))
                }
            } else {
                Result.failure(Exception(payResponse.errorBody()?.string() ?: "Payment initialization failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addReview(
        jobId: Long = 1L,
        workerId: Long,
        workerName: String,
        clientName: String,
        rating: Int,
        comment: String,
        serviceType: String
    ): Result<Boolean> {
        return try {
            val response = api.createReview(
                CreateReviewRequest(
                    job = jobId,
                    rating = rating,
                    comment = comment
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                reviewDao.insertReview(
                    ReviewEntity(
                        id = dto.id,
                        workerId = dto.target ?: workerId,
                        workerName = dto.targetUsername ?: workerName,
                        clientName = dto.reviewerUsername ?: clientName,
                        rating = dto.rating,
                        comment = dto.comment,
                        date = "Just now",
                        serviceType = serviceType
                    )
                )
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to submit review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // TRADE PROFILES & SERVICE REQUESTS NETWORK CALLS
    // ==========================================

    suspend fun fetchTradeProfilesByCategory(category: String): Result<List<TradeProfile>> {
        return try {
            val response = api.getTradeProfilesByCategory(category)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                tradeProfileDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to load profiles for $category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMyTradeProfiles(): Result<List<TradeProfile>> {
        return try {
            val response = api.getMyTradeProfiles()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                tradeProfileDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to load my trade profiles"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTradeProfile(
        workerId: Long,
        workerName: String,
        displayName: String,
        tradeCategory: String,
        skills: String,
        experienceDesc: String,
        years: Int,
        availability: String,
        tools: String,
        languages: String,
        rate: Double
    ): Result<TradeProfile> {
        return try {
            val response = api.createTradeProfile(
                CreateTradeProfileRequest(
                    displayName = displayName,
                    tradeCategory = tradeCategory,
                    skills = skills,
                    experienceDescription = experienceDesc,
                    yearsOfExperience = years,
                    availability = availability,
                    toolsEquipment = tools,
                    languages = languages
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                tradeProfileDao.insertProfile(dto.toEntity())
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create trade profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTradeProfile(id: Long): Result<Boolean> {
        return try {
            val response = api.deleteTradeProfile(id)
            if (response.isSuccessful) {
                tradeProfileDao.deleteProfile(id)
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete trade profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createServiceRequest(
        clientId: Long,
        clientName: String,
        workerId: Long,
        workerName: String,
        tradeProfileId: Long,
        tradeCategory: String,
        date: String,
        timeSlot: String,
        description: String,
        budget: Double
    ): Result<ServiceRequest> {
        return try {
            val response = api.createServiceRequest(
                CreateServiceRequestDto(
                    tradeProfileId = tradeProfileId,
                    description = description,
                    preferredDate = date,
                    preferredTimeSlot = timeSlot,
                    budget = budget
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                serviceRequestDao.insertRequest(dto.toEntity())
                refreshNotifications()
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to send booking request"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchServiceRequests(): Result<List<ServiceRequest>> {
        return try {
            val response = api.getMyServiceRequests()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                serviceRequestDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to load service requests"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondToServiceRequest(requestId: Long, accept: Boolean, notes: String?): Result<Boolean> {
        return try {
            val response = api.respondToServiceRequest(
                id = requestId,
                request = RespondServiceRequestDto(
                    status = if (accept) "accepted" else "rejected",
                    workerNotes = notes
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                serviceRequestDao.updateRequest(dto.toEntity())
                refreshNotifications()
                Result.success(true)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update service request"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // NOTIFICATIONS NETWORK CALLS
    // ==========================================

    suspend fun refreshNotifications(): Result<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                notificationDao.insertAll(list.map { it.toEntity() })
                Result.success(list.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(id: Long) {
        try {
            api.markNotificationRead(id)
            notificationDao.markAsRead(id)
        } catch (_: Exception) {
            notificationDao.markAsRead(id)
        }
    }

    suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllAsRead()
    }

    // ==========================================
    // WALLET & KYC
    // ==========================================

    suspend fun requestPayout(amount: Double, bankName: String, account: String, ifsc: String): Boolean {
        return try {
            val response = api.requestPayout(
                PayoutRequest(
                    bankName = bankName,
                    bankAccountNumber = account,
                    bankIfsc = ifsc
                )
            )
            if (response.isSuccessful) {
                refreshCurrentUser()
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun updateKycDocuments(idType: String) {
        // KYC documents are submitted for admin approval via Django
        val current = userDao.getUser(sessionManager.getUserId())
        if (current != null) {
            userDao.updateUser(current.copy(verificationStatus = VerificationStatus.PENDING.name))
        }
    }

    // ==========================================
    // DTO EXTENSIONS / CONVERTERS
    // ==========================================

    private fun UserDto.toDomain(activeRole: UserRole): User {
        val verificationEnum = when (verificationStatus.lowercase()) {
            "verified" -> VerificationStatus.VERIFIED
            "pending" -> VerificationStatus.PENDING
            "rejected" -> VerificationStatus.REJECTED
            else -> VerificationStatus.UNSUBMITTED
        }
        return User(
            id = id,
            username = username,
            email = email ?: "",
            phoneNumber = phoneNumber ?: "",
            isClient = isClient,
            isWorker = isWorker,
            activeRole = activeRole,
            verificationStatus = verificationEnum,
            rejectionReason = rejectionReason,
            walletBalance = walletBalance?.toDoubleOrNull() ?: 0.0,
            bankName = bankName ?: "State Bank of India",
            accountNumber = bankAccountNumber ?: "XXXX-XXXX-1234",
            ifscCode = bankIfsc ?: "SBIN0001234",
            rating = 4.8f,
            jobsCompleted = 10
        )
    }

    private fun JobDto.toEntity(): JobEntity {
        return JobEntity(
            id = id,
            title = title,
            description = description,
            serviceType = serviceType,
            latitude = latitude,
            longitude = longitude,
            address = address,
            budget = budget.toDoubleOrNull() ?: 0.0,
            isNegotiable = isNegotiable,
            urgencyLevel = if (urgencyLevel.equals("Emergency", true)) "EMERGENCY" else "STANDARD",
            status = when (status.lowercase()) {
                "accepted" -> "ACCEPTED"
                "worker_completed" -> "WORKER_COMPLETED"
                "completed" -> "COMPLETED"
                "disputed" -> "DISPUTED"
                else -> "PENDING"
            },
            escrowStatus = when (escrowStatus.lowercase()) {
                "held" -> "HELD"
                "released" -> "RELEASED"
                "refunded" -> "REFUNDED"
                "pending" -> "PENDING"
                else -> "NONE"
            },
            clientId = client ?: 1L,
            clientName = clientUsername ?: "Client",
            workerId = worker,
            workerName = workerUsername,
            createdAt = System.currentTimeMillis(),
            distanceKm = distanceKm ?: 2.0
        )
    }

    private fun JobDto.toDomain(): Job = toEntity().toDomain()

    private fun TradeProfileDto.toEntity(): TradeProfileEntity {
        return TradeProfileEntity(
            id = id,
            workerId = worker ?: 1L,
            workerName = workerUsername ?: "Worker",
            displayName = displayName,
            tradeCategory = tradeCategory,
            skills = skills,
            experienceDescription = experienceDescription,
            yearsOfExperience = yearsOfExperience,
            availability = availability,
            toolsEquipment = toolsEquipment ?: "",
            languages = languages ?: "",
            rating = (averageRating ?: 4.8).toFloat(),
            reviewCount = reviewCount ?: 0,
            isActive = isActive,
            hourlyRate = 450.0
        )
    }

    private fun TradeProfileDto.toDomain(): TradeProfile = toEntity().toDomain()

    private fun ServiceRequestDto.toEntity(): ServiceRequestEntity {
        return ServiceRequestEntity(
            id = id,
            clientId = client ?: 1L,
            clientName = clientUsername ?: "Client",
            workerId = worker ?: 1L,
            workerName = workerUsername ?: "Worker",
            tradeProfileId = tradeProfile ?: 1L,
            tradeCategory = tradeCategory ?: "General",
            date = preferredDate,
            timeSlot = preferredTimeSlot,
            description = description,
            budget = budget?.toDoubleOrNull() ?: 0.0,
            status = when (status.lowercase()) {
                "accepted" -> "ACCEPTED"
                "rejected" -> "REJECTED"
                "worker_completed" -> "WORKER_COMPLETED"
                "completed" -> "COMPLETED"
                else -> "PENDING"
            },
            escrowStatus = when (escrowStatus.lowercase()) {
                "held" -> "HELD"
                "released" -> "RELEASED"
                "refunded" -> "REFUNDED"
                "pending" -> "PENDING"
                else -> "NONE"
            },
            workerNotes = workerNotes,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun ServiceRequestDto.toDomain(): ServiceRequest = toEntity().toDomain()

    private fun NotificationDto.toEntity(): NotificationEntity {
        return NotificationEntity(
            id = id,
            title = title,
            message = message,
            type = "general",
            isRead = isRead,
            timestamp = System.currentTimeMillis(),
            relatedJobId = null
        )
    }

    private fun NotificationDto.toDomain(): Notification = toEntity().toDomain()
}

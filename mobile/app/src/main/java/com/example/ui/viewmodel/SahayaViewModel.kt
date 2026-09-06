package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Job
import com.example.data.model.Notification
import com.example.data.model.Review
import com.example.data.model.ServiceRequest
import com.example.data.model.TradeProfile
import com.example.data.model.UrgencyLevel
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.network.SessionManager
import com.example.data.repository.SahayaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination {
    // Auth
    LANDING,
    LOGIN,
    REGISTER,

    // Client
    CLIENT_DASHBOARD,
    POST_JOB,
    MY_JOBS,
    JOB_DETAIL,
    BROWSE_TRADES,
    WORKER_PROFILE_DETAIL,
    BOOK_SERVICE,
    MY_REQUESTS,

    // Worker
    WORKER_MARKETPLACE,
    ACTIVE_JOB,
    MY_PROFILES,
    CREATE_PROFILE,
    INCOMING_REQUESTS,
    WALLET,

    // Shared
    NOTIFICATIONS,
    PROFILE,
    KYC_UPLOAD,
    VERIFICATION_PENDING
}

enum class ClientTab {
    HOME,
    BROWSE,
    REQUESTS,
    NOTIFICATIONS,
    PROFILE
}

enum class WorkerTab {
    FIND_JOBS,
    ACTIVE_JOB,
    PROFILES,
    WALLET,
    PROFILE
}

data class RazorpayPaymentState(
    val isOpen: Boolean = false,
    val jobId: Long? = null,
    val requestId: Long? = null,
    val title: String = "",
    val amount: Double = 0.0,
    val orderId: String = "",
    val isVerifying: Boolean = false,
    val isSuccess: Boolean = false
)

class SahayaViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val database = AppDatabase.getDatabase(application)
    private val repository = SahayaRepository(database, sessionManager)

    // User & Session
    val currentUser: StateFlow<User?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Language Toggle (false = English, true = Hindi)
    private val _isHindi = MutableStateFlow(false)
    val isHindi: StateFlow<Boolean> = _isHindi.asStateFlow()

    // Navigation & Tabs
    private val _currentScreen = MutableStateFlow(
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.getActiveRole() == UserRole.CLIENT) AppDestination.CLIENT_DASHBOARD
            else AppDestination.WORKER_MARKETPLACE
        } else {
            AppDestination.LANDING
        }
    )
    val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

    private val _clientTab = MutableStateFlow(ClientTab.HOME)
    val clientTab: StateFlow<ClientTab> = _clientTab.asStateFlow()

    private val _workerTab = MutableStateFlow(WorkerTab.FIND_JOBS)
    val workerTab: StateFlow<WorkerTab> = _workerTab.asStateFlow()

    // Loading & Network State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Selected items for detail screens
    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob.asStateFlow()

    private val _selectedWorker = MutableStateFlow<TradeProfile?>(null)
    val selectedWorker: StateFlow<TradeProfile?> = _selectedWorker.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Jobs
    val availableJobs: StateFlow<List<Job>> = repository.availableJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientJobs: StateFlow<List<Job>> = repository.getClientJobs(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workerActiveJob: StateFlow<Job?> = repository.getWorkerActiveJob(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workerHistory: StateFlow<List<Job>> = repository.getWorkerHistory(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profiles & Requests
    val allTradeProfiles: StateFlow<List<TradeProfile>> = repository.allTradeProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myTradeProfiles: StateFlow<List<TradeProfile>> = repository.getMyTradeProfiles(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientRequests: StateFlow<List<ServiceRequest>> = repository.getClientRequests(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workerRequests: StateFlow<List<ServiceRequest>> = repository.getWorkerRequests(sessionManager.getUserId())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Razorpay Checkout Dialog State
    private val _razorpayState = MutableStateFlow(RazorpayPaymentState())
    val razorpayState: StateFlow<RazorpayPaymentState> = _razorpayState.asStateFlow()

    // Base URL Setting
    private val _baseUrl = MutableStateFlow(sessionManager.getBaseUrl())
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    // Snackbar / Toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        if (sessionManager.isLoggedIn()) {
            refreshAllData()
        }
    }

    fun setBaseUrl(url: String) {
        sessionManager.saveBaseUrl(url)
        _baseUrl.value = sessionManager.getBaseUrl()
        showMessage("Backend URL updated to: ${_baseUrl.value}")
        refreshAllData()
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // --- Authentication & Session ---

    fun login(username: String, password: String, targetRole: UserRole = UserRole.CLIENT) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(username, password)
            _isLoading.value = false
            result.onSuccess { user ->
                switchRole(targetRole)
                showMessage("Welcome back, ${user.username}!")
                refreshAllData()
            }.onFailure { err ->
                showMessage(err.message ?: "Login failed. Please check your credentials.")
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        phone: String,
        isClient: Boolean,
        isWorker: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(
                username = username,
                email = email,
                password = password,
                firstName = firstName,
                phoneNumber = phone,
                isClient = isClient,
                isWorker = isWorker
            )
            _isLoading.value = false
            result.onSuccess { msg ->
                showMessage("Account created! Please log in.")
                _currentScreen.value = AppDestination.LOGIN
            }.onFailure { err ->
                showMessage(err.message ?: "Registration failed.")
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = AppDestination.LANDING
        showMessage("Logged out successfully.")
    }

    fun refreshAllData() {
        viewModelScope.launch {
            repository.refreshCurrentUser()
            repository.fetchAvailableJobs()
            repository.fetchClientJobs()
            repository.fetchWorkerActiveJob()
            repository.fetchWorkerHistory()
            repository.fetchMyTradeProfiles()
            repository.fetchServiceRequests()
            repository.refreshNotifications()
        }
    }

    // --- Navigation Controls ---

    fun navigateTo(dest: AppDestination) {
        _currentScreen.value = dest
        viewModelScope.launch {
            when (dest) {
                AppDestination.WORKER_MARKETPLACE -> repository.fetchAvailableJobs()
                AppDestination.MY_JOBS -> repository.fetchClientJobs()
                AppDestination.ACTIVE_JOB -> repository.fetchWorkerActiveJob()
                AppDestination.NOTIFICATIONS -> repository.refreshNotifications()
                AppDestination.MY_PROFILES -> repository.fetchMyTradeProfiles()
                AppDestination.MY_REQUESTS -> repository.fetchServiceRequests()
                else -> {}
            }
        }
    }

    fun setClientTab(tab: ClientTab) {
        _clientTab.value = tab
        when (tab) {
            ClientTab.HOME -> navigateTo(AppDestination.CLIENT_DASHBOARD)
            ClientTab.BROWSE -> navigateTo(AppDestination.BROWSE_TRADES)
            ClientTab.REQUESTS -> navigateTo(AppDestination.MY_REQUESTS)
            ClientTab.NOTIFICATIONS -> navigateTo(AppDestination.NOTIFICATIONS)
            ClientTab.PROFILE -> navigateTo(AppDestination.PROFILE)
        }
    }

    fun setWorkerTab(tab: WorkerTab) {
        _workerTab.value = tab
        when (tab) {
            WorkerTab.FIND_JOBS -> navigateTo(AppDestination.WORKER_MARKETPLACE)
            WorkerTab.ACTIVE_JOB -> navigateTo(AppDestination.ACTIVE_JOB)
            WorkerTab.PROFILES -> navigateTo(AppDestination.MY_PROFILES)
            WorkerTab.WALLET -> navigateTo(AppDestination.WALLET)
            WorkerTab.PROFILE -> navigateTo(AppDestination.PROFILE)
        }
    }

    fun selectJob(job: Job) {
        _selectedJob.value = job
        _currentScreen.value = AppDestination.JOB_DETAIL
    }

    fun selectWorker(profile: TradeProfile) {
        _selectedWorker.value = profile
        _currentScreen.value = AppDestination.WORKER_PROFILE_DETAIL
    }

    fun selectCategory(catId: String) {
        _selectedCategory.value = catId
        viewModelScope.launch {
            repository.fetchTradeProfilesByCategory(catId)
        }
        _currentScreen.value = AppDestination.BROWSE_TRADES
    }

    fun toggleLanguage() {
        _isHindi.value = !_isHindi.value
    }

    fun switchRole(newRole: UserRole) {
        viewModelScope.launch {
            repository.switchUserRole(newRole)
            if (newRole == UserRole.CLIENT) {
                setClientTab(ClientTab.HOME)
            } else {
                setWorkerTab(WorkerTab.FIND_JOBS)
            }
            showMessage(if (newRole == UserRole.CLIENT) "Switched to Client Mode" else "Switched to Worker Mode")
        }
    }

    // --- Job Actions ---

    fun postJob(
        title: String,
        description: String,
        serviceType: String,
        address: String,
        budget: Double,
        isNegotiable: Boolean,
        urgency: UrgencyLevel,
        lat: Double = 18.5204,
        lon: Double = 73.8567
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = currentUser.value
            val result = repository.postJob(
                title = title,
                description = description,
                serviceType = serviceType,
                latitude = lat,
                longitude = lon,
                address = address,
                budget = budget,
                isNegotiable = isNegotiable,
                urgencyLevel = urgency,
                clientId = user?.id ?: sessionManager.getUserId(),
                clientName = user?.username ?: sessionManager.getUsername()
            )
            _isLoading.value = false
            result.onSuccess {
                showMessage("Job posted successfully! Workers alerted nearby.")
                setClientTab(ClientTab.HOME)
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to post job.")
            }
        }
    }

    fun acceptJob(job: Job) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user?.verificationStatus != VerificationStatus.VERIFIED) {
                _currentScreen.value = AppDestination.VERIFICATION_PENDING
                return@launch
            }
            _isLoading.value = true
            val result = repository.acceptJob(
                jobId = job.id,
                workerId = user.id,
                workerName = user.username
            )
            _isLoading.value = false
            result.onSuccess {
                showMessage("Job accepted! Awaiting client escrow funding.")
                setWorkerTab(WorkerTab.ACTIVE_JOB)
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to accept job.")
            }
        }
    }

    fun openRazorpayCheckout(job: Job) {
        val orderId = "order_rzp_" + System.currentTimeMillis().toString().takeLast(6)
        _razorpayState.value = RazorpayPaymentState(
            isOpen = true,
            jobId = job.id,
            title = job.title,
            amount = job.budget,
            orderId = orderId
        )
    }

    fun completeRazorpayPayment() {
        val state = _razorpayState.value
        if (state.jobId == null) return
        _razorpayState.value = state.copy(isVerifying = true)
        viewModelScope.launch {
            val result = repository.fundJobEscrow(state.jobId)
            result.onSuccess {
                _razorpayState.value = state.copy(isVerifying = false, isSuccess = true)
                showMessage("Payment of ₹${state.amount.toInt()} secured in Escrow!")
                kotlinx.coroutines.delay(800)
                _razorpayState.value = RazorpayPaymentState()
            }.onFailure { err ->
                _razorpayState.value = state.copy(isVerifying = false)
                showMessage(err.message ?: "Payment verification failed.")
            }
        }
    }

    fun dismissRazorpay() {
        _razorpayState.value = RazorpayPaymentState()
    }

    fun workerMarkComplete(job: Job) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.workerMarkJobComplete(job.id)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Marked as complete! Client requested to release funds.")
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to mark as complete.")
            }
        }
    }

    fun clientApproveAndRelease(job: Job) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.clientApproveAndRelease(job.id)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Escrow released! ₹${job.budget.toInt()} credited to worker.")
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to release escrow.")
            }
        }
    }

    fun clientDispute(job: Job, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.clientDisputeJob(job.id, reason)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Dispute registered. Support team will mediate.")
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to register dispute.")
            }
        }
    }

    fun submitReview(workerId: Long, workerName: String, rating: Int, comment: String, serviceType: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val currentJobId = selectedJob.value?.id ?: 1L
            val result = repository.addReview(
                jobId = currentJobId,
                workerId = workerId,
                workerName = workerName,
                clientName = user?.username ?: "Verified Client",
                rating = rating,
                comment = comment,
                serviceType = serviceType
            )
            result.onSuccess {
                showMessage("Thank you! Review submitted.")
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to submit review.")
            }
        }
    }

    // --- Book Service Request ---

    fun bookService(
        worker: TradeProfile,
        date: String,
        timeSlot: String,
        description: String,
        budget: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = currentUser.value
            val result = repository.createServiceRequest(
                clientId = user?.id ?: sessionManager.getUserId(),
                clientName = user?.username ?: sessionManager.getUsername(),
                workerId = worker.workerId,
                workerName = worker.workerName,
                tradeProfileId = worker.id,
                tradeCategory = worker.tradeCategory,
                date = date,
                timeSlot = timeSlot,
                description = description,
                budget = budget
            )
            _isLoading.value = false
            result.onSuccess {
                showMessage("Direct booking sent to ${worker.workerName}!")
                setClientTab(ClientTab.REQUESTS)
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to send booking request.")
            }
        }
    }

    fun respondServiceRequest(requestId: Long, accept: Boolean, notes: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.respondToServiceRequest(requestId, accept, notes)
            _isLoading.value = false
            result.onSuccess {
                showMessage(if (accept) "Request accepted!" else "Request declined.")
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to respond to request.")
            }
        }
    }

    // --- Trade Profile ---

    fun createTradeProfile(
        displayName: String,
        category: String,
        skills: String,
        experienceDesc: String,
        years: Int,
        availability: String,
        tools: String,
        languages: String,
        rate: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = currentUser.value
            val result = repository.createTradeProfile(
                workerId = user?.id ?: sessionManager.getUserId(),
                workerName = user?.username ?: sessionManager.getUsername(),
                displayName = displayName,
                tradeCategory = category,
                skills = skills,
                experienceDesc = experienceDesc,
                years = years,
                availability = availability,
                tools = tools,
                languages = languages,
                rate = rate
            )
            _isLoading.value = false
            result.onSuccess {
                showMessage("Trade profile published to marketplace!")
                setWorkerTab(WorkerTab.PROFILES)
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to create trade profile.")
            }
        }
    }

    fun deleteTradeProfile(id: Long) {
        viewModelScope.launch {
            repository.deleteTradeProfile(id)
            showMessage("Trade profile deleted.")
        }
    }

    // --- KYC Documents ---

    fun submitKyc(idType: String) {
        viewModelScope.launch {
            repository.updateKycDocuments(idType)
            showMessage("KYC documents uploaded. Status updated to Pending.")
            _currentScreen.value = AppDestination.VERIFICATION_PENDING
        }
    }

    // --- Wallet Payout ---

    fun requestPayout(amount: Double, bankName: String, account: String, ifsc: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.requestPayout(amount, bankName, account, ifsc)
            _isLoading.value = false
            if (success) {
                showMessage("Payout request of ₹${amount.toInt()} initiated!")
            } else {
                showMessage("Payout request failed. Check balance or credentials.")
            }
        }
    }

    // --- Notifications ---

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read.")
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.RazorpayPaymentDialog
import com.example.ui.screens.auth.LandingScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.client.BookServiceScreen
import com.example.ui.screens.client.BrowseTradesScreen
import com.example.ui.screens.client.ClientDashboardScreen
import com.example.ui.screens.client.JobDetailScreen
import com.example.ui.screens.client.MyServiceRequestsScreen
import com.example.ui.screens.client.PostJobScreen
import com.example.ui.screens.client.WorkerProfileDetailScreen
import com.example.ui.screens.shared.KYCUploadScreen
import com.example.ui.screens.shared.NotificationsScreen
import com.example.ui.screens.shared.ProfileScreen
import com.example.ui.screens.shared.VerificationPendingScreen
import com.example.ui.screens.worker.ActiveJobScreen
import com.example.ui.screens.worker.CreateTradeProfileScreen
import com.example.ui.screens.worker.IncomingRequestsScreen
import com.example.ui.screens.worker.MyTradeProfilesScreen
import com.example.ui.screens.worker.WalletScreen
import com.example.ui.screens.worker.WorkerMarketplaceScreen
import com.example.ui.theme.BentoOutline
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SahayaPrimary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ClientTab
import com.example.ui.viewmodel.SahayaViewModel
import com.example.ui.viewmodel.WorkerTab

class MainActivity : ComponentActivity() {

    private val viewModel: SahayaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SahayaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SahayaApp(viewModel: SahayaViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val clientTab by viewModel.clientTab.collectAsState()
    val workerTab by viewModel.workerTab.collectAsState()

    val availableJobs by viewModel.availableJobs.collectAsState()
    val clientJobs by viewModel.clientJobs.collectAsState()
    val workerActiveJob by viewModel.workerActiveJob.collectAsState()
    val allProfiles by viewModel.allTradeProfiles.collectAsState()
    val myProfiles by viewModel.myTradeProfiles.collectAsState()
    val clientRequests by viewModel.clientRequests.collectAsState()
    val workerRequests by viewModel.workerRequests.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    val selectedJob by viewModel.selectedJob.collectAsState()
    val selectedWorker by viewModel.selectedWorker.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val razorpayState by viewModel.razorpayState.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val baseUrl by viewModel.baseUrl.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    val isAuthScreen = currentScreen == AppDestination.LANDING ||
        currentScreen == AppDestination.LOGIN ||
        currentScreen == AppDestination.REGISTER

    val activeRole = currentUser?.activeRole ?: UserRole.CLIENT

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isAuthScreen) {
                if (activeRole == UserRole.CLIENT) {
                    ClientBottomNavigation(
                        currentTab = clientTab,
                        onTabSelected = { viewModel.setClientTab(it) },
                        unreadAlerts = unreadCount,
                        isHindi = isHindi
                    )
                } else {
                    WorkerBottomNavigation(
                        currentTab = workerTab,
                        onTabSelected = { viewModel.setWorkerTab(it) },
                        unreadAlerts = unreadCount,
                        isHindi = isHindi
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppDestination.LANDING -> {
                    LandingScreen(
                        onSelectRole = { role ->
                            viewModel.switchRole(role)
                            if (role == UserRole.CLIENT) {
                                viewModel.navigateTo(AppDestination.CLIENT_DASHBOARD)
                            } else {
                                viewModel.navigateTo(AppDestination.WORKER_MARKETPLACE)
                            }
                        },
                        onLoginClick = { viewModel.navigateTo(AppDestination.LOGIN) },
                        onRegisterClick = { viewModel.navigateTo(AppDestination.REGISTER) },
                        isHindi = isHindi,
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )
                }

                AppDestination.LOGIN -> {
                    LoginScreen(
                        onLoginSuccess = { username, password, role ->
                            viewModel.login(username, password, role)
                        },
                        onBackClick = { viewModel.navigateTo(AppDestination.LANDING) },
                        onRegisterClick = { viewModel.navigateTo(AppDestination.REGISTER) },
                        isHindi = isHindi
                    )
                }

                AppDestination.REGISTER -> {
                    RegisterScreen(
                        onRegisterSuccess = { username, email, password, firstName, phone, isClient, isWorker ->
                            viewModel.register(username, email, password, firstName, phone, isClient, isWorker)
                        },
                        onBackClick = { viewModel.navigateTo(AppDestination.LANDING) },
                        onLoginClick = { viewModel.navigateTo(AppDestination.LOGIN) },
                        isHindi = isHindi
                    )
                }

                // Client Screens
                AppDestination.CLIENT_DASHBOARD -> {
                    ClientDashboardScreen(
                        user = currentUser,
                        clientJobs = clientJobs,
                        onPostJobClick = { viewModel.navigateTo(AppDestination.POST_JOB) },
                        onBrowseTradesClick = { viewModel.setClientTab(ClientTab.BROWSE) },
                        onCategoryClick = { catId -> viewModel.selectCategory(catId) },
                        onJobClick = { job -> viewModel.selectJob(job) },
                        onEmergencyPostClick = { viewModel.navigateTo(AppDestination.POST_JOB) },
                        isHindi = isHindi
                    )
                }

                AppDestination.POST_JOB -> {
                    PostJobScreen(
                        onPostJob = { title, desc, cat, addr, budget, neg, urgency ->
                            viewModel.postJob(title, desc, cat, addr, budget, neg, urgency)
                        },
                        onBackClick = { viewModel.setClientTab(ClientTab.HOME) },
                        isHindi = isHindi
                    )
                }

                AppDestination.BROWSE_TRADES -> {
                    BrowseTradesScreen(
                        profiles = allProfiles,
                        selectedCategoryKey = selectedCategory,
                        onCategorySelected = { cat -> viewModel.selectCategory(cat ?: "") },
                        onWorkerClick = { worker -> viewModel.selectWorker(worker) },
                        onBookWorkerClick = { worker ->
                            viewModel.selectWorker(worker)
                            viewModel.navigateTo(AppDestination.BOOK_SERVICE)
                        },
                        isHindi = isHindi
                    )
                }

                AppDestination.WORKER_PROFILE_DETAIL -> {
                    WorkerProfileDetailScreen(
                        profile = selectedWorker,
                        onBackClick = { viewModel.navigateTo(AppDestination.BROWSE_TRADES) },
                        onBookServiceClick = { worker ->
                            viewModel.selectWorker(worker)
                            viewModel.navigateTo(AppDestination.BOOK_SERVICE)
                        },
                        isHindi = isHindi
                    )
                }

                AppDestination.BOOK_SERVICE -> {
                    BookServiceScreen(
                        worker = selectedWorker,
                        onBookService = { worker, date, slot, desc, budget ->
                            viewModel.bookService(worker, date, slot, desc, budget)
                        },
                        onBackClick = { viewModel.navigateTo(AppDestination.WORKER_PROFILE_DETAIL) },
                        isHindi = isHindi
                    )
                }

                AppDestination.MY_REQUESTS -> {
                    MyServiceRequestsScreen(
                        requests = clientRequests,
                        isHindi = isHindi
                    )
                }

                // Worker Screens
                AppDestination.WORKER_MARKETPLACE -> {
                    WorkerMarketplaceScreen(
                        user = currentUser,
                        jobs = availableJobs,
                        onJobClick = { job -> viewModel.selectJob(job) },
                        onAcceptJobClick = { job -> viewModel.acceptJob(job) },
                        isHindi = isHindi
                    )
                }

                AppDestination.ACTIVE_JOB -> {
                    ActiveJobScreen(
                        activeJob = workerActiveJob,
                        onMarkDoneClick = { job -> viewModel.workerMarkComplete(job) },
                        onFindJobsClick = { viewModel.setWorkerTab(WorkerTab.FIND_JOBS) },
                        isHindi = isHindi
                    )
                }

                AppDestination.MY_PROFILES -> {
                    MyTradeProfilesScreen(
                        profiles = myProfiles,
                        onCreateProfileClick = { viewModel.navigateTo(AppDestination.CREATE_PROFILE) },
                        onDeleteProfileClick = { id -> viewModel.deleteTradeProfile(id) },
                        isHindi = isHindi
                    )
                }

                AppDestination.CREATE_PROFILE -> {
                    CreateTradeProfileScreen(
                        onSubmit = { name, cat, skills, desc, yrs, avail, tools, langs, rate ->
                            viewModel.createTradeProfile(name, cat, skills, desc, yrs, avail, tools, langs, rate)
                        },
                        onBackClick = { viewModel.setWorkerTab(WorkerTab.PROFILES) },
                        isHindi = isHindi
                    )
                }

                AppDestination.INCOMING_REQUESTS -> {
                    IncomingRequestsScreen(
                        requests = workerRequests,
                        onRespondClick = { reqId, accept, notes ->
                            viewModel.respondServiceRequest(reqId, accept, notes)
                        },
                        isHindi = isHindi
                    )
                }

                AppDestination.WALLET -> {
                    WalletScreen(
                        user = currentUser,
                        onRequestPayout = { amount, bank, acc, ifsc ->
                            viewModel.requestPayout(amount, bank, acc, ifsc)
                        },
                        isHindi = isHindi
                    )
                }

                // Shared Screens
                AppDestination.JOB_DETAIL -> {
                    JobDetailScreen(
                        job = selectedJob,
                        activeRole = activeRole,
                        onBackClick = {
                            if (activeRole == UserRole.CLIENT) {
                                viewModel.setClientTab(ClientTab.HOME)
                            } else {
                                viewModel.setWorkerTab(WorkerTab.FIND_JOBS)
                            }
                        },
                        onPayEscrowClick = { job -> viewModel.openRazorpayCheckout(job) },
                        onWorkerAcceptClick = { job -> viewModel.acceptJob(job) },
                        onWorkerMarkCompleteClick = { job -> viewModel.workerMarkComplete(job) },
                        onClientApproveReleaseClick = { job -> viewModel.clientApproveAndRelease(job) },
                        onClientDisputeClick = { job, reason -> viewModel.clientDispute(job, reason) },
                        onSubmitReview = { wId, wName, rating, comment, serviceType ->
                            viewModel.submitReview(wId, wName, rating, comment, serviceType)
                        },
                        isHindi = isHindi
                    )
                }

                AppDestination.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { notif -> viewModel.markNotificationRead(notif.id) },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        isHindi = isHindi
                    )
                }

                AppDestination.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        onSwitchRole = { role -> viewModel.switchRole(role) },
                        onKycClick = { viewModel.navigateTo(AppDestination.KYC_UPLOAD) },
                        onWalletClick = {
                            if (activeRole == UserRole.WORKER) {
                                viewModel.setWorkerTab(WorkerTab.WALLET)
                            } else {
                                viewModel.navigateTo(AppDestination.WALLET)
                            }
                        },
                        isHindi = isHindi,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        baseUrl = baseUrl,
                        onUpdateBaseUrl = { viewModel.setBaseUrl(it) },
                        onLogoutClick = { viewModel.logout() }
                    )
                }

                AppDestination.KYC_UPLOAD -> {
                    KYCUploadScreen(
                        onSubmit = { idType -> viewModel.submitKyc(idType) },
                        onBackClick = {
                            if (activeRole == UserRole.CLIENT) {
                                viewModel.setClientTab(ClientTab.PROFILE)
                            } else {
                                viewModel.setWorkerTab(WorkerTab.PROFILE)
                            }
                        },
                        isHindi = isHindi
                    )
                }

                AppDestination.VERIFICATION_PENDING -> {
                    VerificationPendingScreen(
                        status = currentUser?.verificationStatus ?: VerificationStatus.PENDING,
                        rejectionReason = currentUser?.rejectionReason,
                        onReUploadClick = { viewModel.navigateTo(AppDestination.KYC_UPLOAD) },
                        onContinueClick = {
                            if (activeRole == UserRole.CLIENT) {
                                viewModel.setClientTab(ClientTab.HOME)
                            } else {
                                viewModel.setWorkerTab(WorkerTab.FIND_JOBS)
                            }
                        },
                        isHindi = isHindi
                    )
                }

                else -> {
                    ClientDashboardScreen(
                        user = currentUser,
                        clientJobs = clientJobs,
                        onPostJobClick = { viewModel.navigateTo(AppDestination.POST_JOB) },
                        onBrowseTradesClick = { viewModel.setClientTab(ClientTab.BROWSE) },
                        onCategoryClick = { catId -> viewModel.selectCategory(catId) },
                        onJobClick = { job -> viewModel.selectJob(job) },
                        onEmergencyPostClick = { viewModel.navigateTo(AppDestination.POST_JOB) },
                        isHindi = isHindi
                    )
                }
            }

            // Razorpay Payment Modal
            RazorpayPaymentDialog(
                state = razorpayState,
                onPayClicked = { viewModel.completeRazorpayPayment() },
                onDismiss = { viewModel.dismissRazorpay() },
                isHindi = isHindi
            )
        }
    }
}

@Composable
fun ClientBottomNavigation(
    currentTab: ClientTab,
    onTabSelected: (ClientTab) -> Unit,
    unreadAlerts: Int,
    isHindi: Boolean = false
) {
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = BentoPrimary,
        selectedTextColor = BentoPrimary,
        indicatorColor = BentoPrimaryContainer,
        unselectedIconColor = BentoTextSecondary,
        unselectedTextColor = BentoTextSecondary
    )

    NavigationBar(
        containerColor = BentoSurfaceVariant,
        tonalElevation = 0.dp,
        modifier = Modifier.border(1.dp, BentoOutline.copy(alpha = 0.35f))
    ) {
        NavigationBarItem(
            selected = currentTab == ClientTab.HOME,
            onClick = { onTabSelected(ClientTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(if (isHindi) "होम" else "Home", fontSize = 10.sp, fontWeight = if (currentTab == ClientTab.HOME) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_client_home")
        )

        NavigationBarItem(
            selected = currentTab == ClientTab.BROWSE,
            onClick = { onTabSelected(ClientTab.BROWSE) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
            label = { Text(if (isHindi) "कारीगर" else "Trades", fontSize = 10.sp, fontWeight = if (currentTab == ClientTab.BROWSE) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_client_browse")
        )

        NavigationBarItem(
            selected = currentTab == ClientTab.REQUESTS,
            onClick = { onTabSelected(ClientTab.REQUESTS) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Requests") },
            label = { Text(if (isHindi) "अनुरोध" else "Bookings", fontSize = 10.sp, fontWeight = if (currentTab == ClientTab.REQUESTS) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_client_requests")
        )

        NavigationBarItem(
            selected = currentTab == ClientTab.NOTIFICATIONS,
            onClick = { onTabSelected(ClientTab.NOTIFICATIONS) },
            icon = {
                if (unreadAlerts > 0) {
                    BadgedBox(badge = { Badge { Text("$unreadAlerts") } }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                    }
                } else {
                    Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                }
            },
            label = { Text(if (isHindi) "सूचनाएं" else "Alerts", fontSize = 10.sp, fontWeight = if (currentTab == ClientTab.NOTIFICATIONS) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_client_notifications")
        )

        NavigationBarItem(
            selected = currentTab == ClientTab.PROFILE,
            onClick = { onTabSelected(ClientTab.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text(if (isHindi) "प्रोफ़ाइल" else "Profile", fontSize = 10.sp, fontWeight = if (currentTab == ClientTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_client_profile")
        )
    }
}

@Composable
fun WorkerBottomNavigation(
    currentTab: WorkerTab,
    onTabSelected: (WorkerTab) -> Unit,
    unreadAlerts: Int,
    isHindi: Boolean = false
) {
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = BentoPrimary,
        selectedTextColor = BentoPrimary,
        indicatorColor = BentoPrimaryContainer,
        unselectedIconColor = BentoTextSecondary,
        unselectedTextColor = BentoTextSecondary
    )

    NavigationBar(
        containerColor = BentoSurfaceVariant,
        tonalElevation = 0.dp,
        modifier = Modifier.border(1.dp, BentoOutline.copy(alpha = 0.35f))
    ) {
        NavigationBarItem(
            selected = currentTab == WorkerTab.FIND_JOBS,
            onClick = { onTabSelected(WorkerTab.FIND_JOBS) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Radar") },
            label = { Text(if (isHindi) "मार्केटप्लेस" else "Jobs Radar", fontSize = 10.sp, fontWeight = if (currentTab == WorkerTab.FIND_JOBS) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_worker_jobs")
        )

        NavigationBarItem(
            selected = currentTab == WorkerTab.ACTIVE_JOB,
            onClick = { onTabSelected(WorkerTab.ACTIVE_JOB) },
            icon = { Icon(Icons.Default.Work, contentDescription = "Active") },
            label = { Text(if (isHindi) "सक्रिय कार्य" else "Active Job", fontSize = 10.sp, fontWeight = if (currentTab == WorkerTab.ACTIVE_JOB) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_worker_active")
        )

        NavigationBarItem(
            selected = currentTab == WorkerTab.PROFILES,
            onClick = { onTabSelected(WorkerTab.PROFILES) },
            icon = { Icon(Icons.Default.Handyman, contentDescription = "Profiles") },
            label = { Text(if (isHindi) "ट्रेड (3)" else "Profiles", fontSize = 10.sp, fontWeight = if (currentTab == WorkerTab.PROFILES) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_worker_profiles")
        )

        NavigationBarItem(
            selected = currentTab == WorkerTab.WALLET,
            onClick = { onTabSelected(WorkerTab.WALLET) },
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Wallet") },
            label = { Text(if (isHindi) "वॉलेट" else "Wallet", fontSize = 10.sp, fontWeight = if (currentTab == WorkerTab.WALLET) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_worker_wallet")
        )

        NavigationBarItem(
            selected = currentTab == WorkerTab.PROFILE,
            onClick = { onTabSelected(WorkerTab.PROFILE) },
            icon = {
                if (unreadAlerts > 0) {
                    BadgedBox(badge = { Badge { Text("$unreadAlerts") } }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
            },
            label = { Text(if (isHindi) "खाता" else "Profile", fontSize = 10.sp, fontWeight = if (currentTab == WorkerTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tab_worker_profile")
        )
    }
}

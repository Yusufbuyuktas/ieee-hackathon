package com.hackathon_ieee.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hackathon_ieee.myapplication.feature.auth.AuthWelcomeScreen
import com.hackathon_ieee.myapplication.feature.auth.LoginScreen
import com.hackathon_ieee.myapplication.feature.auth.RegisterScreen
import com.hackathon_ieee.myapplication.feature.auth.SplashScreen
import com.hackathon_ieee.myapplication.feature.home.HomeScreen
import com.hackathon_ieee.myapplication.feature.map.RiskMapScreen
import com.hackathon_ieee.myapplication.feature.more.MoreScreen
import com.hackathon_ieee.myapplication.feature.profile.ProfileScreen
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportFormScreen
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportReviewScreen
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportStatusScreen
import com.hackathon_ieee.myapplication.core.network.CitizenReport
import com.hackathon_ieee.myapplication.core.network.ApiException
import com.hackathon_ieee.myapplication.core.network.RiverGuardApi
import com.hackathon_ieee.myapplication.core.storage.LocalReportRepository
import com.hackathon_ieee.myapplication.core.storage.SavedCitizenReport
import com.hackathon_ieee.myapplication.ui.components.BottomDestination
import com.hackathon_ieee.myapplication.ui.components.RiverBottomBar
import com.hackathon_ieee.myapplication.ui.components.RiverGuardWordmark
import com.hackathon_ieee.myapplication.ui.components.ThickBackIcon
import kotlinx.coroutines.launch

private const val HOME_SCREEN = "home"
private const val MAP_SCREEN = "map"
private const val REPORT_FORM_SCREEN = "report_form"
private const val REPORT_REVIEW_SCREEN = "report_review"
private const val REPORT_STATUS_SCREEN = "report_status"
private const val PROFILE_SCREEN = "profile"
private const val MORE_SCREEN = "more"
private const val SPLASH_STAGE = "splash"
private const val AUTH_WELCOME_STAGE = "auth_welcome"
private const val LOGIN_STAGE = "login"
private const val REGISTER_STAGE = "register"
private const val APP_STAGE = "app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneAquaHealthApp() {
    val context = LocalContext.current
    val localReportRepository = remember {
        LocalReportRepository(context)
    }
    val api = remember(context) { RiverGuardApi(context) }
    val coroutineScope = rememberCoroutineScope()
    var appStage by rememberSaveable {
        mutableStateOf(SPLASH_STAGE)
    }
    var signedInEmail by rememberSaveable {
        mutableStateOf("")
    }
    var signedInFullName by rememberSaveable {
        mutableStateOf("")
    }
    var signedInRole by rememberSaveable {
        mutableStateOf("")
    }
    var authenticationError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var isAuthenticating by remember {
        mutableStateOf(false)
    }
    var currentScreen by rememberSaveable {
        mutableStateOf(HOME_SCREEN)
    }
    var isShowingAllLocations by rememberSaveable {
        mutableStateOf(false)
    }
    var isHomeDetailVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val screenStateHolder = rememberSaveableStateHolder()

    var reviewPhotoUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var reviewCategoryName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var reviewLatitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }
    var reviewLongitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }
    var reviewNote by rememberSaveable {
        mutableStateOf("")
    }
    var reportDraftVersion by rememberSaveable {
        mutableStateOf(0)
    }
    var reportReviewVersion by rememberSaveable {
        mutableStateOf(0)
    }
    var submittedReportId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var submittedReportStatus by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var submittedReportMatchScore by rememberSaveable {
        mutableStateOf<Double?>(null)
    }
    var savedReports by remember {
        mutableStateOf(emptyList<SavedCitizenReport>())
    }
    var isReportsRefreshing by remember {
        mutableStateOf(false)
    }
    var reportsRefreshMessage by remember {
        mutableStateOf<String?>(null)
    }

    suspend fun refreshReports(email: String) {
        if (email.isBlank()) return

        val localReports = localReportRepository.getReports(email)
        savedReports = localReports
        isReportsRefreshing = true
        reportsRefreshMessage = null

        api.getCitizenReports().fold(
            onSuccess = { remoteReports ->
                val syncedReports = mergeReports(email, localReports, remoteReports)
                localReportRepository.replaceForOwner(email, syncedReports)
                savedReports = syncedReports
            },
            onFailure = {
                reportsRefreshMessage = if (localReports.isEmpty()) {
                    "Reports could not be loaded. Please try again."
                } else {
                    "Could not refresh. Showing reports saved on this device."
                }
            }
        )
        isReportsRefreshing = false
    }

    LaunchedEffect(signedInEmail, appStage, currentScreen) {
        savedReports = if (signedInEmail.isBlank()) {
            emptyList()
        } else {
            localReportRepository.getReports(signedInEmail)
        }
        if (
            signedInEmail.isNotBlank() &&
            appStage == APP_STAGE &&
            currentScreen == PROFILE_SCREEN
        ) {
            refreshReports(signedInEmail)
        }
    }

    if (appStage == SPLASH_STAGE) {
        SplashScreen(
            onFinished = {
                if (!api.hasSavedSession) {
                    appStage = AUTH_WELCOME_STAGE
                } else {
                    coroutineScope.launch {
                        api.getCurrentUser().fold(
                            onSuccess = { user ->
                                signedInEmail = user.email
                                signedInFullName = user.fullName
                                signedInRole = user.role
                                savedReports = localReportRepository.getReports(user.email)
                                currentScreen = HOME_SCREEN
                                appStage = APP_STAGE
                            },
                            onFailure = {
                                appStage = AUTH_WELCOME_STAGE
                            }
                        )
                    }
                }
            }
        )
        return
    }

    if (appStage == AUTH_WELCOME_STAGE) {
        AuthWelcomeScreen(
            onLogin = {
                authenticationError = null
                appStage = LOGIN_STAGE
            },
            onRegister = {
                authenticationError = null
                appStage = REGISTER_STAGE
            }
        )
        return
    }

    if (appStage == LOGIN_STAGE) {
        BackHandler {
            appStage = AUTH_WELCOME_STAGE
        }
        LoginScreen(
            onBack = {
                authenticationError = null
                appStage = AUTH_WELCOME_STAGE
            },
            onRegister = {
                authenticationError = null
                appStage = REGISTER_STAGE
            },
            onSignIn = { email, password ->
                coroutineScope.launch {
                    isAuthenticating = true
                    authenticationError = null
                    api.login(email = email, password = password).fold(
                        onSuccess = { user ->
                            signedInEmail = user.email
                            signedInFullName = user.fullName
                            signedInRole = user.role
                            savedReports = localReportRepository.getReports(user.email)
                            currentScreen = HOME_SCREEN
                            appStage = APP_STAGE
                        },
                        onFailure = { error ->
                            authenticationError = error.toAuthenticationMessage(
                                defaultMessage = "Sign in failed. Please try again."
                            )
                        }
                    )
                    isAuthenticating = false
                }
            },
            isSubmitting = isAuthenticating,
            authenticationError = authenticationError,
            onInputChanged = {
                authenticationError = null
            }
        )
        return
    }

    if (appStage == REGISTER_STAGE) {
        BackHandler {
            appStage = AUTH_WELCOME_STAGE
        }
        RegisterScreen(
            onBack = {
                authenticationError = null
                appStage = AUTH_WELCOME_STAGE
            },
            onLogin = {
                authenticationError = null
                appStage = LOGIN_STAGE
            },
            onRegistered = { fullName, email, password ->
                coroutineScope.launch {
                    isAuthenticating = true
                    authenticationError = null
                    api.register(
                        fullName = fullName,
                        email = email,
                        password = password
                    ).fold(
                        onSuccess = { user ->
                            signedInEmail = user.email
                            signedInFullName = user.fullName
                            signedInRole = user.role
                            savedReports = localReportRepository.getReports(user.email)
                            currentScreen = HOME_SCREEN
                            appStage = APP_STAGE
                        },
                        onFailure = { error ->
                            authenticationError = error.toAuthenticationMessage(
                                defaultMessage = "Account creation failed. Please try again."
                            )
                        }
                    )
                    isAuthenticating = false
                }
            },
            isSubmitting = isAuthenticating,
            registrationError = authenticationError,
            onInputChanged = {
                authenticationError = null
            }
        )
        return
    }

    BackHandler(
        enabled = currentScreen == REPORT_FORM_SCREEN ||
            currentScreen == REPORT_REVIEW_SCREEN ||
            currentScreen == REPORT_STATUS_SCREEN
    ) {
        currentScreen = when (currentScreen) {
            REPORT_REVIEW_SCREEN -> REPORT_FORM_SCREEN
            else -> HOME_SCREEN
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (
                !(currentScreen == HOME_SCREEN && isHomeDetailVisible)
            ) {
                Surface(
                    modifier = Modifier.zIndex(1f),
                    shape = RoundedCornerShape(
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 15.dp
                ) {
                    TopAppBar(
                        modifier = Modifier.height(100.dp),
                        title = {
                            if (
                                currentScreen == HOME_SCREEN ||
                                (currentScreen == MAP_SCREEN && !isShowingAllLocations) ||
                                currentScreen == PROFILE_SCREEN ||
                                currentScreen == MORE_SCREEN
                            ) {
                                RiverGuardWordmark(
                                    modifier = Modifier.width(180.dp)
                                )
                            } else {
                                Text(
                                    text = when (currentScreen) {
                                        MAP_SCREEN -> "Monitoring Locations"
                                        REPORT_REVIEW_SCREEN -> "Review Report"
                                        REPORT_STATUS_SCREEN -> "Report Status"
                                        else -> "New Report"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            navigationIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            if (
                                currentScreen == REPORT_REVIEW_SCREEN ||
                                (currentScreen == MAP_SCREEN && isShowingAllLocations)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (currentScreen == MAP_SCREEN) {
                                            isShowingAllLocations = false
                                        } else {
                                            currentScreen = REPORT_FORM_SCREEN
                                        }
                                    }
                                ) {
                                    ThickBackIcon(color = Color.White)
                                }
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (
                currentScreen != REPORT_REVIEW_SCREEN &&
                currentScreen != REPORT_STATUS_SCREEN &&
                !(currentScreen == HOME_SCREEN && isHomeDetailVisible)
            ) {
                RiverBottomBar(
                    selectedDestination = currentScreen.toBottomDestination(),
                    onDestinationSelected = { destination ->
                        isShowingAllLocations = false
                        isHomeDetailVisible = false
                        currentScreen = destination.toScreenName()
                    }
                )
            }
        }
    ) { innerPadding ->
        val screenStateKey = when (currentScreen) {
            REPORT_FORM_SCREEN -> "$currentScreen-$reportDraftVersion"
            REPORT_REVIEW_SCREEN -> "$currentScreen-$reportReviewVersion"
            else -> currentScreen
        }
        screenStateHolder.SaveableStateProvider(screenStateKey) {
            when (currentScreen) {
                HOME_SCREEN -> {
                    HomeScreen(
                        onDetailVisibilityChanged = { isHomeDetailVisible = it },
                        onReportClick = { currentScreen = REPORT_FORM_SCREEN },
                        modifier = if (isHomeDetailVisible) {
                            Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        } else {
                            Modifier.padding(innerPadding)
                        }
                    )
                }

                MAP_SCREEN -> {
                    RiskMapScreen(
                        api = api,
                        userRole = signedInRole,
                        showAllLocations = isShowingAllLocations,
                        onShowAllLocationsChange = { isShowingAllLocations = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                REPORT_FORM_SCREEN -> {
                    ReportFormScreen(
                        onContinue = { photoUri, category, latitude, longitude, note ->
                            reviewPhotoUri = photoUri
                            reviewCategoryName = category.name
                            reviewLatitude = latitude
                            reviewLongitude = longitude
                            reviewNote = note
                            reportReviewVersion++
                            currentScreen = REPORT_REVIEW_SCREEN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                REPORT_REVIEW_SCREEN -> {
                    val photoUri = reviewPhotoUri
                    val categoryName = reviewCategoryName
                    val latitude = reviewLatitude
                    val longitude = reviewLongitude

                    if (
                        photoUri != null &&
                        categoryName != null
                    ) {
                        ReportReviewScreen(
                            photoUri = photoUri,
                            category = ReportCategory.valueOf(categoryName),
                            latitude = latitude,
                            longitude = longitude,
                            note = reviewNote,
                            onEdit = {
                                currentScreen = REPORT_FORM_SCREEN
                            },
                            onSubmitted = { submission ->
                                val submittedCategory = ReportCategory.valueOf(categoryName)
                                localReportRepository.save(
                                    SavedCitizenReport(
                                        id = submission.id,
                                        ownerEmail = signedInEmail,
                                        category = submittedCategory.displayName,
                                        note = reviewNote,
                                        latitude = latitude,
                                        longitude = longitude,
                                        submittedAtMillis = System.currentTimeMillis(),
                                        aiValidationStatus = submission.aiValidationStatus,
                                        aiMatchScore = submission.aiMatchScore
                                    )
                                )
                                savedReports = localReportRepository.getReports(signedInEmail)

                                submittedReportId = submission.id
                                submittedReportStatus = submission.aiValidationStatus
                                submittedReportMatchScore = submission.aiMatchScore

                                reviewPhotoUri = null
                                reviewCategoryName = null
                                reviewLatitude = null
                                reviewLongitude = null
                                reviewNote = ""
                                reportDraftVersion++
                                currentScreen = REPORT_STATUS_SCREEN
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                REPORT_STATUS_SCREEN -> {
                    val reportId = submittedReportId
                    val reportStatus = submittedReportStatus
                    if (reportId != null && reportStatus != null) {
                        ReportStatusScreen(
                            reportId = reportId,
                            aiValidationStatus = reportStatus,
                            aiMatchScore = submittedReportMatchScore,
                            onBackHome = {
                                currentScreen = HOME_SCREEN
                            },
                            onSubmitAgain = {
                                currentScreen = REPORT_FORM_SCREEN
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                PROFILE_SCREEN -> {
                    ProfileScreen(
                        fullName = signedInFullName,
                        email = signedInEmail,
                        role = signedInRole,
                        reports = savedReports,
                        isRefreshing = isReportsRefreshing,
                        refreshMessage = reportsRefreshMessage,
                        onRefresh = {
                            coroutineScope.launch {
                                refreshReports(signedInEmail)
                            }
                        },
                        onLogout = {
                            coroutineScope.launch {
                                api.logout()
                            }
                            signedInEmail = ""
                            signedInFullName = ""
                            signedInRole = ""
                            authenticationError = null
                            savedReports = emptyList()
                            reportsRefreshMessage = null
                            currentScreen = HOME_SCREEN
                            appStage = AUTH_WELCOME_STAGE
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MORE_SCREEN -> {
                    MoreScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private fun Throwable.toAuthenticationMessage(defaultMessage: String): String = when (this) {
    is ApiException -> when (statusCode) {
        400 -> message ?: "Please check the information you entered."
        401 -> "Incorrect email or password."
        409 -> "An account with this email already exists."
        else -> message ?: defaultMessage
    }

    is java.net.ConnectException,
    is java.net.SocketTimeoutException,
    is java.net.UnknownHostException -> "Could not connect to RiverGuard. Please try again."

    else -> defaultMessage
}

private fun mergeReports(
    ownerEmail: String,
    localReports: List<SavedCitizenReport>,
    remoteReports: List<CitizenReport>
): List<SavedCitizenReport> {
    val localById = localReports.associateBy { it.id }
    val remoteIds = remoteReports.mapTo(mutableSetOf()) { it.id }
    val syncedRemoteReports = remoteReports.map { remoteReport ->
        val localReport = localById[remoteReport.id]
        SavedCitizenReport(
            id = remoteReport.id,
            ownerEmail = ownerEmail,
            category = remoteReport.category.toCategoryLabel(),
            note = remoteReport.note,
            latitude = remoteReport.latitude,
            longitude = remoteReport.longitude,
            submittedAtMillis = localReport?.submittedAtMillis
                ?: remoteReport.timestamp.toReportEpochMillis(),
            aiValidationStatus = remoteReport.aiValidationStatus,
            aiMatchScore = remoteReport.aiMatchScore,
            photoUrl = remoteReport.photoUrl,
            aiExplanation = remoteReport.aiExplanation,
            fhirObservationId = remoteReport.fhirObservationId
        )
    }
    val localOnlyReports = localReports.filterNot { it.id in remoteIds }
    return (syncedRemoteReports + localOnlyReports)
        .sortedByDescending { it.submittedAtMillis }
}

private fun String.toReportEpochMillis(): Long =
    runCatching { java.time.Instant.parse(this).toEpochMilli() }
        .recoverCatching { java.time.OffsetDateTime.parse(this).toInstant().toEpochMilli() }
        .recoverCatching {
            java.time.LocalDateTime.parse(this)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
        .getOrDefault(0L)

private fun String.toCategoryLabel(): String = when (uppercase()) {
    "BULANIK", "BULANIK_SU" -> "Turbid water"
    "KIRLI_RENK_DEGISIMI" -> "Water discoloration"
    "BALIK_OLUMU" -> "Fish mortality"
    "KOTU_KOKU" -> "Bad odor"
    "DIGER" -> "Other"
    else -> replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
}

private fun String.toBottomDestination(): BottomDestination = when (this) {
    MAP_SCREEN -> BottomDestination.MAP
    REPORT_FORM_SCREEN -> BottomDestination.REPORT
    PROFILE_SCREEN -> BottomDestination.PROFILE
    MORE_SCREEN -> BottomDestination.MORE
    else -> BottomDestination.HOME
}

private fun BottomDestination.toScreenName(): String = when (this) {
    BottomDestination.HOME -> HOME_SCREEN
    BottomDestination.MAP -> MAP_SCREEN
    BottomDestination.REPORT -> REPORT_FORM_SCREEN
    BottomDestination.PROFILE -> PROFILE_SCREEN
    BottomDestination.MORE -> MORE_SCREEN
}

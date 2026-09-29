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
import com.hackathon_ieee.myapplication.feature.auth.LoginScreen
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
private const val LOGIN_STAGE = "login"
private const val APP_STAGE = "app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneAquaHealthApp() {
    val context = LocalContext.current
    val localReportRepository = remember {
        LocalReportRepository(context)
    }
    val api = remember { RiverGuardApi() }
    val coroutineScope = rememberCoroutineScope()
    var appStage by rememberSaveable {
        mutableStateOf(SPLASH_STAGE)
    }
    var signedInEmail by rememberSaveable {
        mutableStateOf("")
    }
    var currentScreen by rememberSaveable {
        mutableStateOf(HOME_SCREEN)
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
                val syncedReports = mergeReports(localReports, remoteReports)
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
                appStage = LOGIN_STAGE
            }
        )
        return
    }

    if (appStage == LOGIN_STAGE) {
        LoginScreen(
            onSignIn = { email ->
                signedInEmail = email
                savedReports = localReportRepository.getReports(email)
                currentScreen = HOME_SCREEN
                appStage = APP_STAGE
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
                            currentScreen == MAP_SCREEN ||
                            currentScreen == PROFILE_SCREEN ||
                            currentScreen == MORE_SCREEN
                        ) {
                            RiverGuardWordmark(
                                modifier = Modifier.width(180.dp)
                            )
                        } else {
                            Text(
                                text = when (currentScreen) {
                                    REPORT_REVIEW_SCREEN -> "Review Report"
                                    REPORT_STATUS_SCREEN -> "Report Status"
                                    else -> "New Report"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        if (currentScreen == REPORT_REVIEW_SCREEN) {
                            IconButton(
                                onClick = {
                                    currentScreen = REPORT_FORM_SCREEN
                                }
                            ) {
                                ThickBackIcon()
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (
                currentScreen != REPORT_REVIEW_SCREEN &&
                currentScreen != REPORT_STATUS_SCREEN
            ) {
                RiverBottomBar(
                    selectedDestination = currentScreen.toBottomDestination(),
                    onDestinationSelected = { destination ->
                        currentScreen = destination.toScreenName()
                    }
                )
            }
        }
    ) { innerPadding ->
        val screenStateKey = if (currentScreen == REPORT_FORM_SCREEN) {
            "$currentScreen-$reportDraftVersion"
        } else {
            currentScreen
        }
        screenStateHolder.SaveableStateProvider(screenStateKey) {
            when (currentScreen) {
                HOME_SCREEN -> {
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MAP_SCREEN -> {
                    RiskMapScreen(
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
                        categoryName != null &&
                        latitude != null &&
                        longitude != null
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
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                PROFILE_SCREEN -> {
                    ProfileScreen(
                        email = signedInEmail,
                        reports = savedReports,
                        isRefreshing = isReportsRefreshing,
                        refreshMessage = reportsRefreshMessage,
                        onRefresh = {
                            coroutineScope.launch {
                                refreshReports(signedInEmail)
                            }
                        },
                        onLogout = {
                            signedInEmail = ""
                            savedReports = emptyList()
                            reportsRefreshMessage = null
                            currentScreen = HOME_SCREEN
                            appStage = LOGIN_STAGE
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

private fun mergeReports(
    localReports: List<SavedCitizenReport>,
    remoteReports: List<CitizenReport>
): List<SavedCitizenReport> {
    val remoteById = remoteReports.associateBy { it.id }
    return localReports.map { localReport ->
        val remoteReport = remoteById[localReport.id] ?: return@map localReport
        localReport.copy(
            category = remoteReport.category.toCategoryLabel(),
            note = remoteReport.note,
            latitude = remoteReport.latitude,
            longitude = remoteReport.longitude,
            aiValidationStatus = remoteReport.aiValidationStatus,
            aiMatchScore = remoteReport.aiMatchScore,
            photoUrl = remoteReport.photoUrl
        )
    }.sortedByDescending { it.submittedAtMillis }
}

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

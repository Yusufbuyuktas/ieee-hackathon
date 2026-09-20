package com.hackathon_ieee.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hackathon_ieee.myapplication.feature.home.HomeScreen
import com.hackathon_ieee.myapplication.feature.map.RiskMapScreen
import com.hackathon_ieee.myapplication.feature.more.MoreScreen
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportFormScreen
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportReviewScreen
import com.hackathon_ieee.myapplication.feature.reports.MyReportsScreen
import com.hackathon_ieee.myapplication.ui.components.BottomDestination
import com.hackathon_ieee.myapplication.ui.components.RiverBottomBar
import com.hackathon_ieee.myapplication.ui.components.ThickBackIcon

private const val HOME_SCREEN = "home"
private const val MAP_SCREEN = "map"
private const val REPORT_FORM_SCREEN = "report_form"
private const val REPORT_REVIEW_SCREEN = "report_review"
private const val MY_REPORTS_SCREEN = "my_reports"
private const val MORE_SCREEN = "more"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneAquaHealthApp() {
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

    BackHandler(
        enabled = currentScreen == REPORT_FORM_SCREEN ||
            currentScreen == REPORT_REVIEW_SCREEN
    ) {
        currentScreen = if (currentScreen == REPORT_REVIEW_SCREEN) {
            REPORT_FORM_SCREEN
        } else {
            HOME_SCREEN
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
                        Text(
                            text = when (currentScreen) {
                                HOME_SCREEN -> "RiverGuard"
                                MAP_SCREEN -> "Risk Map"
                                MY_REPORTS_SCREEN -> "My Reports"
                                MORE_SCREEN -> "More"
                                REPORT_REVIEW_SCREEN -> "Review Report"
                                else -> "New Report"
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        if (
                            currentScreen == REPORT_FORM_SCREEN ||
                            currentScreen == REPORT_REVIEW_SCREEN
                        ) {
                            IconButton(
                                onClick = {
                                    currentScreen = if (currentScreen == REPORT_REVIEW_SCREEN) {
                                        REPORT_FORM_SCREEN
                                    } else {
                                        HOME_SCREEN
                                    }
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
            if (currentScreen != REPORT_REVIEW_SCREEN) {
                RiverBottomBar(
                    selectedDestination = currentScreen.toBottomDestination(),
                    onDestinationSelected = { destination ->
                        currentScreen = destination.toScreenName()
                    }
                )
            }
        }
    ) { innerPadding ->
        screenStateHolder.SaveableStateProvider(currentScreen) {
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
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                MY_REPORTS_SCREEN -> {
                    MyReportsScreen(
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

private fun String.toBottomDestination(): BottomDestination = when (this) {
    MAP_SCREEN -> BottomDestination.MAP
    REPORT_FORM_SCREEN -> BottomDestination.REPORT
    MY_REPORTS_SCREEN -> BottomDestination.MY_REPORTS
    MORE_SCREEN -> BottomDestination.MORE
    else -> BottomDestination.HOME
}

private fun BottomDestination.toScreenName(): String = when (this) {
    BottomDestination.HOME -> HOME_SCREEN
    BottomDestination.MAP -> MAP_SCREEN
    BottomDestination.REPORT -> REPORT_FORM_SCREEN
    BottomDestination.MY_REPORTS -> MY_REPORTS_SCREEN
    BottomDestination.MORE -> MORE_SCREEN
}

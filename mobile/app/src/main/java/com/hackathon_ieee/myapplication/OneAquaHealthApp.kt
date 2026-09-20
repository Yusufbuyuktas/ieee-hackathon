package com.hackathon_ieee.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import com.hackathon_ieee.myapplication.feature.home.HomeScreen
import androidx.compose.material3.IconButton
import com.hackathon_ieee.myapplication.feature.report.presentation.ReportFormScreen
import com.hackathon_ieee.myapplication.ui.components.ThickBackIcon

private const val HOME_SCREEN = "home"
private const val REPORT_FORM_SCREEN = "report_form"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneAquaHealthApp() {
    var currentScreen by rememberSaveable {
        mutableStateOf(HOME_SCREEN)
    }
    val screenStateHolder = rememberSaveableStateHolder()

    BackHandler(
        enabled = currentScreen != HOME_SCREEN
    ) {
        currentScreen = HOME_SCREEN
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentScreen == HOME_SCREEN) {
                            "RiverGuard"
                        } else {
                            "New Report"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    if (currentScreen != HOME_SCREEN) {
                        IconButton(
                            onClick = {
                                currentScreen = HOME_SCREEN
                            }
                        ) {
                            ThickBackIcon()
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentScreen == HOME_SCREEN,
                    onClick = {
                        currentScreen = HOME_SCREEN
                    },
                    icon = {
                        Text(text = "⌂")
                    },
                    label = {
                        Text(text = "Home")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == REPORT_FORM_SCREEN,
                    onClick = {
                        currentScreen = REPORT_FORM_SCREEN
                    },
                    icon = {
                        Text(text = "+")
                    },
                    label = {
                        Text(text = "Report")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->
        screenStateHolder.SaveableStateProvider(currentScreen) {
            when (currentScreen) {
                HOME_SCREEN -> {
                    HomeScreen(
                        onCreateReportClick = {
                            currentScreen = REPORT_FORM_SCREEN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                REPORT_FORM_SCREEN -> {
                    ReportFormScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

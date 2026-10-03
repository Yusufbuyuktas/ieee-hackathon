package com.hackathon_ieee.myapplication.feature.report.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.core.location.LocationProvider
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory
import com.hackathon_ieee.myapplication.feature.report.presentation.components.LocationMap
import com.hackathon_ieee.myapplication.feature.report.presentation.components.PhotoInputCard
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import com.hackathon_ieee.myapplication.ui.theme.RiverGlassLow
import com.hackathon_ieee.myapplication.ui.theme.RiverSuccess

private enum class LocationUiState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

@Composable
fun ReportFormScreen(
    onContinue: (
        photoUri: String,
        category: ReportCategory,
        latitude: Double?,
        longitude: Double?,
        note: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryName by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var note by rememberSaveable {
        mutableStateOf("")
    }

    var selectedPhotoUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var latitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var longitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var locationStateName by rememberSaveable {
        mutableStateOf(LocationUiState.IDLE.name)
    }

    var locationMessage by rememberSaveable {
        mutableStateOf("")
    }

    val noteFocusRequester = androidx.compose.runtime.remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val locationProvider = androidx.compose.runtime.remember(context) {
        LocationProvider(context)
    }

    fun requestCurrentLocation() {
        locationStateName = LocationUiState.LOADING.name
        locationMessage = "Retrieving your current location..."

        locationProvider.getCurrentLocation(
            onSuccess = { location ->
                latitude = location.latitude
                longitude = location.longitude
                locationStateName = LocationUiState.SUCCESS.name
                locationMessage = "Current location captured."
            },
            onError = { message ->
                latitude = null
                longitude = null
                locationStateName = LocationUiState.ERROR.name
                locationMessage = message
            }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val permissionGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permissionGranted) {
            requestCurrentLocation()
        } else {
            locationStateName = LocationUiState.ERROR.name
            locationMessage = "Location permission was denied."
        }
    }

    val selectedCategory = selectedCategoryName?.let { categoryName ->
        ReportCategory.valueOf(categoryName)
    }
    val isOtherSelected = selectedCategory == ReportCategory.OTHER
    val isRequiredDescriptionMissing = isOtherSelected && note.isBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Photo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        PhotoInputCard(
            selectedPhotoUri = selectedPhotoUri,
            onPhotoSelected = { photoUri ->
                selectedPhotoUri = photoUri
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SubtlePanel {
            Column {
                ReportCategory.entries.forEachIndexed { index, category ->
                    val isSelected = selectedCategory == category
                    val categoryBackground = when {
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        index % 2 == 0 -> RiverGlassLow
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(categoryBackground)
                            .clickable {
                                selectedCategoryName = category.name
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryName = category.name
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = if (category == ReportCategory.OTHER) {
                                "${category.displayName} — description required"
                            } else {
                                category.displayName
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SubtlePanel {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                when (LocationUiState.valueOf(locationStateName)) {
                    LocationUiState.LOADING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )

                            Text(text = locationMessage)
                        }
                    }

                    LocationUiState.SUCCESS -> {
                        val currentLatitude = latitude
                        val currentLongitude = longitude

                        Text(
                            text = locationMessage,
                            color = RiverSuccess,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (currentLatitude != null && currentLongitude != null) {
                            LocationMap(
                                latitude = currentLatitude,
                                longitude = currentLongitude
                            )
                        }
                    }

                    LocationUiState.ERROR -> {
                        Text(
                            text = locationMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    LocationUiState.IDLE -> {
                        if (locationMessage.isNotBlank()) {
                            Text(text = locationMessage)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        if (locationProvider.hasLocationPermission()) {
                            requestCurrentLocation()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = locationStateName != LocationUiState.LOADING.name,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (latitude == null || longitude == null) {
                            "Use Current Location"
                        } else {
                            "Refresh Location"
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = if (isOtherSelected) {
                "Description (required)"
            } else {
                "Additional note"
            },
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = { newValue ->
                if (newValue.length <= 500) {
                    note = newValue
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
                .focusRequester(noteFocusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        noteFocusRequester.requestFocus()
                        keyboardController?.show()
                    }
                },
            enabled = true,
            readOnly = false,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            label = {
                Text(
                    text = if (isOtherSelected) {
                        "Describe your observation (required)"
                    } else {
                        "Describe your observation"
                    }
                )
            },
            placeholder = {
                if (isOtherSelected) {
                    Text("Tell us what you observed")
                }
            },
            isError = isRequiredDescriptionMissing,
            supportingText = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isRequiredDescriptionMissing) {
                        Text(
                            text = "A description is required when Other is selected.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "${note.length}/500",
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            },
            minLines = 4,
            maxLines = 7
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                val photoUri = selectedPhotoUri
                val category = selectedCategory
                val currentLatitude = latitude
                val currentLongitude = longitude

                if (
                    photoUri != null &&
                    category != null &&
                    (!isOtherSelected || note.isNotBlank())
                ) {
                    onContinue(
                        photoUri,
                        category,
                        currentLatitude,
                        currentLongitude,
                        note.trim()
                    )
                }
            },
            enabled = selectedPhotoUri != null &&
                selectedCategory != null &&
                !isRequiredDescriptionMissing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Continue"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

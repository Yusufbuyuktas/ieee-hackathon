package com.hackathon_ieee.myapplication.feature.map

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.core.location.DeviceLocation
import com.hackathon_ieee.myapplication.core.location.LocationProvider
import com.hackathon_ieee.myapplication.core.network.ApiException
import com.hackathon_ieee.myapplication.core.network.MonitoringLocation
import com.hackathon_ieee.myapplication.core.network.Observation
import com.hackathon_ieee.myapplication.core.network.RiskAssessment
import com.hackathon_ieee.myapplication.core.network.RiskStatus
import com.hackathon_ieee.myapplication.core.network.RiverGuardApi
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import com.hackathon_ieee.myapplication.ui.theme.RiverDanger
import com.hackathon_ieee.myapplication.ui.theme.RiverSuccess
import com.hackathon_ieee.myapplication.ui.theme.RiverWarning
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskMapScreen(
    api: RiverGuardApi,
    userRole: String,
    showAllLocations: Boolean,
    onShowAllLocationsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationProvider = remember { LocationProvider(context) }
    val canViewClinicalData = userRole == "DOCTOR"

    var locations by remember { mutableStateOf(emptyList<MonitoringLocation>()) }
    var observations by remember { mutableStateOf(emptyList<Observation>()) }
    var assessments by remember { mutableStateOf(emptyList<RiskAssessment>()) }
    var selectedLocation by remember { mutableStateOf<MonitoringLocation?>(null) }
    var riskStatus by remember { mutableStateOf<RiskStatus?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRiskLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var showLocationDetails by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var isPullRefreshing by remember { mutableStateOf(false) }
    val locationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val riskyLocationNames = observations.filter { it.riskFlagged }
        .mapTo(mutableSetOf()) { it.locationName }
    val sortedLocations = remember(locations) {
        locations.sortedWith { first, second ->
            compareNaturally(
                first.name.toEnglishLocationName(),
                second.name.toEnglishLocationName()
            )
        }
    }

    BackHandler(enabled = showAllLocations) {
        onShowAllLocationsChange(false)
    }

    fun selectNearest(deviceLocation: DeviceLocation) {
        val nearest = locations.filter { it.coordinates != null }.minByOrNull { location ->
            val point = checkNotNull(location.coordinates)
            distanceKm(deviceLocation.latitude, deviceLocation.longitude, point.latitude, point.longitude)
        }
        if (nearest == null) {
            locationMessage = "No monitoring point with coordinates is available."
        } else {
            selectedLocation = nearest
            val point = checkNotNull(nearest.coordinates)
            val distance = distanceKm(
                deviceLocation.latitude, deviceLocation.longitude, point.latitude, point.longitude
            )
            locationMessage = "Nearest point: ${nearest.name.toEnglishLocationName()} (${formatNumber(distance)} km away)"
        }
    }

    fun requestCurrentLocation() {
        locationMessage = "Finding the nearest monitoring point…"
        locationProvider.getCurrentLocation(onSuccess = ::selectNearest, onError = { locationMessage = it })
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) requestCurrentLocation()
        else locationMessage = "Location permission is needed to find the nearest point."
    }

    LaunchedEffect(refreshKey, canViewClinicalData) {
        isLoading = true
        errorMessage = null
        try {
            locations = api.getLocations().getOrThrow()
            if (canViewClinicalData) {
                coroutineScope {
                    val observationsRequest = async { api.getObservations().getOrThrow() }
                    val assessmentsRequest = async { api.getRiskAssessments().getOrThrow() }
                    observations = observationsRequest.await()
                    assessments = assessmentsRequest.await()
                }
            } else {
                observations = emptyList()
                assessments = emptyList()
            }
            if (selectedLocation == null || locations.none { it.name == selectedLocation?.name }) {
                selectedLocation = locations.firstOrNull()
            }
        } catch (_: Exception) {
            errorMessage = "RiverGuard data could not be reached. Check that the backend is running and try again."
        } finally {
            isLoading = false
            isPullRefreshing = false
        }
    }

    LaunchedEffect(selectedLocation?.name, refreshKey) {
        val location = selectedLocation ?: return@LaunchedEffect
        isRiskLoading = true
        riskStatus = null
        api.getRiskStatus(location.name)
            .onSuccess { riskStatus = it }
            .onFailure { error ->
                if (error !is ApiException || error.statusCode != 404) {
                    locationMessage = "Risk status for ${location.name.toEnglishLocationName()} could not be loaded."
                }
            }
        isRiskLoading = false
    }

    if (showAllLocations) {
        AllLocationsPage(
            locations = sortedLocations,
            riskyLocationNames = riskyLocationNames,
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                refreshKey++
            },
            onLocationClick = { location ->
                selectedLocation = location
                locationMessage = null
                showLocationDetails = true
            },
            modifier = modifier
        )
    } else {
        PullToRefreshBox(
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                refreshKey++
            },
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                text = if (canViewClinicalData) {
                    "Basin Risk Overview"
                } else {
                    "Basin Monitoring Overview"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                text = "Explore monitoring data across the Ergene Basin and focus on locations that need attention.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                isLoading -> LoadingState()
                errorMessage != null -> ErrorState(checkNotNull(errorMessage)) { refreshKey++ }
                else -> {
                    if (canViewClinicalData) {
                        OverviewPanel(locations, observations)
                        LatestAlertPanel(observations)
                    }

                    SectionTitle("Monitoring Map")
                    if (locations.any { it.coordinates != null }) {
                        RiskLocationsMap(locations, riskyLocationNames, selectedLocation)
                    } else {
                        EmptyPanel("No mapped monitoring locations are available yet.")
                    }

                    Button(
                        onClick = {
                            if (locationProvider.hasLocationPermission()) requestCurrentLocation()
                            else permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = locations.isNotEmpty()
                    ) { Text("Use My Location") }

                    locationMessage?.let {
                        Text(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SectionTitle("Monitoring Locations")
                    if (locations.isEmpty()) {
                        EmptyPanel("No monitoring locations were returned by the API.")
                    } else {
                        LocationList(
                            locations = sortedLocations.take(3),
                            riskyLocationNames = riskyLocationNames,
                            onLocationClick = { location ->
                                selectedLocation = location
                                locationMessage = null
                                showLocationDetails = true
                            }
                        )
                        if (sortedLocations.size > 3) {
                            TextButton(
                                onClick = { onShowAllLocationsChange(true) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("See All Locations")
                            }
                        }
                    }

                }
            }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showLocationDetails && selectedLocation != null) {
        ModalBottomSheet(
            onDismissRequest = { showLocationDetails = false },
            sheetState = locationSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            SelectedLocationPanel(
                location = selectedLocation,
                status = riskStatus,
                assessment = assessments
                    .filter { it.locationName == selectedLocation?.name }
                    .maxByOrNull { it.timestamp },
                isLoading = isRiskLoading,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 28.dp)
                    .height(520.dp)
            )
        }
    }
}

@Composable
private fun AllLocationsPage(
    locations: List<MonitoringLocation>,
    riskyLocationNames: Set<String>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLocationClick: (MonitoringLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LocationList(
                locations = locations,
                riskyLocationNames = riskyLocationNames,
                onLocationClick = onLocationClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LocationList(
    locations: List<MonitoringLocation>,
    riskyLocationNames: Set<String>,
    onLocationClick: (MonitoringLocation) -> Unit
) {
    SubtlePanel {
        Column {
            locations.forEachIndexed { index, location ->
                val isRisky = location.name in riskyLocationNames
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLocationClick(location) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isRisky) RiverDanger else MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = location.name.toEnglishLocationName(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        location.stationNo?.let { station ->
                            Text(
                                text = "Station $station",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "›",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (index < locations.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                    )
                }
            }
        }
    }
}


@Composable
private fun OverviewPanel(locations: List<MonitoringLocation>, observations: List<Observation>) {
    val flagged = observations.count { it.riskFlagged }
    val lastUpdate = observations.maxByOrNull { it.timestamp }?.timestamp?.displayTimestamp() ?: "—"
    SubtlePanel {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Basin Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("Locations", locations.size.toString())
                Metric("Observations", observations.size.toString())
                Metric("Flagged", flagged.toString())
            }
            Text(
                text = "Last update: $lastUpdate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SelectedLocationPanel(
    location: MonitoringLocation?,
    status: RiskStatus?,
    assessment: RiskAssessment?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    SubtlePanel(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                location?.name?.toEnglishLocationName() ?: "No location selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            location?.stationNo?.let {
                Text("Station number: $it", style = MaterialTheme.typography.bodySmall)
            }
            if (location?.sampleTypes?.isNotEmpty() == true) {
                val sampleTypeLabel = if (location.sampleTypes.size == 1) {
                    "Sample type"
                } else {
                    "Sample types"
                }
                Text(
                    "$sampleTypeLabel: ${location.sampleTypes.joinToString { it.toDisplayLabel() }}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                status != null -> {
                    Text(
                        text = status.riskLevel.toRiskLevelLabel(),
                        color = riskColor(status.riskLevel),
                        fontWeight = FontWeight.Bold
                    )
                    Text("${status.parameter.toDisplayLabel()}: ${formatNullable(status.value)} ${status.unit}".trim())
                    status.threshold?.let { Text("Threshold: ${formatNumber(it)} ${status.unit}".trim()) }
                    if (status.reason.isNotBlank()) {
                        Text(
                            status.reason.toEnglishRiskReason(status.parameter),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (status.standard.isNotBlank()) {
                        Text(
                            "Standard: ${status.standard.replace('_', ' ')}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                else -> Text("No active flagged risk was returned for this location.", color = RiverSuccess)
            }
            assessment?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Health Risk Assessment", fontWeight = FontWeight.SemiBold)
                Text("Level: ${it.riskLevel.toRiskLevelLabel()}", color = riskColor(it.riskLevel))
                it.totalHazardIndex?.child?.let { value -> Text("Child hazard index: ${formatNumber(value)}") }
                it.totalHazardIndex?.adult?.let { value -> Text("Adult hazard index: ${formatNumber(value)}") }
                it.basisNote?.takeIf(String::isNotBlank)?.let { note ->
                    Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun LatestAlertPanel(observations: List<Observation>) {
    val alert = observations.filter { it.riskFlagged }.maxByOrNull { it.timestamp }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Latest Environmental Alert")
        if (alert == null) {
            EmptyPanel("No flagged environmental observations were returned.")
        } else {
            SubtlePanel {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        alert.locationName.toEnglishLocationName(),
                        fontWeight = FontWeight.SemiBold,
                        color = RiverDanger
                    )
                    Text("${alert.parameter.toDisplayLabel()}: ${formatNullable(alert.value)} ${alert.unit}".trim())
                    Text(
                        alert.timestamp.displayTimestamp(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text("Loading basin data…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    SubtlePanel {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun EmptyPanel(message: String) {
    SubtlePanel {
        Text(text = message, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun riskColor(level: String) = when {
    level.contains("high", ignoreCase = true) || level.contains("critical", ignoreCase = true) -> RiverDanger
    level.contains("medium", ignoreCase = true) || level.contains("warning", ignoreCase = true) -> RiverWarning
    else -> RiverSuccess
}

private fun formatNullable(value: Double?): String = value?.let(::formatNumber) ?: "Not reported"
private fun formatNumber(value: Double): String = String.format(Locale.US, "%.2f", value)
private fun String.displayTimestamp(): String = replace('T', ' ').substringBeforeLast('.').removeSuffix("Z")

private fun String.toDisplayLabel(): String =
    replace('_', ' ')
        .lowercase(Locale.ROOT)
        .replaceFirstChar { character -> character.titlecase(Locale.ROOT) }

private fun String.toRiskLevelLabel(): String = when (trim().lowercase(Locale.ROOT)) {
    "low" -> "Low risk"
    "medium" -> "Moderate risk"
    "high" -> "High risk"
    "critical" -> "Critical risk"
    "" -> "Not classified"
    else -> toDisplayLabel()
}

private fun String.toEnglishRiskReason(parameter: String): String {
    val normalized = lowercase(Locale.ROOT)
    return if (
        normalized.startsWith("olculen ") ||
        normalized.startsWith("ölçülen ")
    ) {
        "The measured ${parameter.toDisplayLabel().lowercase(Locale.ROOT)} level exceeds the applicable threshold."
    } else {
        this
    }
}

private val naturalSortParts = Regex("\\d+|\\D+")

private fun String.toEnglishLocationName(): String = when {
    startsWith("Ergene Havzasi - Kuyu ") -> {
        "Ergene Basin – Well ${substringAfterLast(' ')}"
    }

    this == "St 1 - Saray Buyukyoncali (Ergene Menba / Referans)" ->
        "Station 1 – Saray Büyük Yoncalı (Ergene Headwaters / Reference)"

    this == "St 1 - yag fabrikasi yani (Corlu/Cerkezkoy ust havza)" ->
        "Station 1 – Near the Oil Factory (Çorlu/Çerkezköy Upper Basin)"

    this == "St 2 - Cerkezkoy OSB Desari Alti" ->
        "Station 2 – Downstream of Çerkezköy OIZ Discharge"

    this == "St 2 - koy ici, sanayiden uzak" ->
        "Station 2 – Village Center, Away from Industry"

    this == "St 3 - Corlu Cayi Ulas Mevkii" ->
        "Station 3 – Çorlu Stream, Ulaş Area"

    this == "St 3 - organize sanayi bolgesi icinde" ->
        "Station 3 – Within the Organized Industrial Zone"

    this == "St 4 - Muratli Karasogutleme Koprusu" ->
        "Station 4 – Muratlı Karasöğütleme Bridge"

    this == "St 4 - organize sanayi bolgesi icinde" ->
        "Station 4 – Within the Organized Industrial Zone"

    this == "St 5 - Adasarhanli yakini, Meric ile birlesmeden once (mansap)" ->
        "Station 5 – Near Adasarhanlı, Before the Meriç Confluence (Downstream)"

    this == "St 5 - Luleburgaz Buyukkaristiran Mevkii" ->
        "Station 5 – Lüleburgaz Büyükkarıştıran Area"

    this == "St 8 - Uzunkopru DSI Koprusu" ->
        "Station 8 – Uzunköprü DSİ Bridge"

    else -> this
}

private fun compareNaturally(first: String, second: String): Int {
    val firstParts = naturalSortParts.findAll(first).map { it.value }.toList()
    val secondParts = naturalSortParts.findAll(second).map { it.value }.toList()

    for (index in 0 until minOf(firstParts.size, secondParts.size)) {
        val firstPart = firstParts[index]
        val secondPart = secondParts[index]
        val firstNumber = firstPart.toLongOrNull()
        val secondNumber = secondPart.toLongOrNull()
        val comparison = if (firstNumber != null && secondNumber != null) {
            firstNumber.compareTo(secondNumber)
        } else {
            firstPart.compareTo(secondPart, ignoreCase = true)
        }
        if (comparison != 0) return comparison
    }

    return firstParts.size.compareTo(secondParts.size)
}

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val latitudeDistance = Math.toRadians(lat2 - lat1)
    val longitudeDistance = Math.toRadians(lon2 - lon1)
    val startLatitude = Math.toRadians(lat1)
    val endLatitude = Math.toRadians(lat2)
    val a = sin(latitudeDistance / 2).pow(2) +
        cos(startLatitude) * cos(endLatitude) * sin(longitudeDistance / 2).pow(2)
    return earthRadiusKm * 2 * asin(sqrt(a))
}

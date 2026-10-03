package com.hackathon_ieee.myapplication.feature.map

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.hackathon_ieee.myapplication.core.map.OpenFreeMapStyleUrl
import com.hackathon_ieee.myapplication.core.network.MonitoringLocation
import com.hackathon_ieee.myapplication.ui.theme.RiverDanger
import com.hackathon_ieee.myapplication.ui.theme.RiverPrimary
import com.hackathon_ieee.myapplication.ui.theme.RiverSuccess
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val NORMAL_SOURCE = "monitoring-normal-source"
private const val RISK_SOURCE = "monitoring-risk-source"
private const val SELECTED_SOURCE = "monitoring-selected-source"
private const val NORMAL_LAYER = "monitoring-normal-layer"
private const val RISK_LAYER = "monitoring-risk-layer"
private const val SELECTED_LAYER = "monitoring-selected-layer"

@Composable
fun RiskLocationsMap(
    locations: List<MonitoringLocation>,
    riskyLocationNames: Set<String>,
    selectedLocation: MonitoringLocation?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleLoaded by remember { mutableStateOf(false) }

    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply {
            isFocusable = false
            isFocusableInTouchMode = false
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            onCreate(null)
            getMapAsync { readyMap ->
                map = readyMap
                readyMap.setMaxZoomPreference(18.0)
                readyMap.setStyle(OpenFreeMapStyleUrl) { style ->
                    style.addSource(GeoJsonSource(NORMAL_SOURCE, emptyFeatures()))
                    style.addSource(GeoJsonSource(RISK_SOURCE, emptyFeatures()))
                    style.addSource(GeoJsonSource(SELECTED_SOURCE, emptyFeatures()))
                    style.addLayer(
                        CircleLayer(NORMAL_LAYER, NORMAL_SOURCE).withProperties(
                            circleRadius(7f), circleColor("#22D3EE"),
                            circleStrokeWidth(2f), circleStrokeColor("#F8FAFC")
                        )
                    )
                    style.addLayer(
                        CircleLayer(RISK_LAYER, RISK_SOURCE).withProperties(
                            circleRadius(8f), circleColor("#EF4444"),
                            circleStrokeWidth(2f), circleStrokeColor("#F8FAFC")
                        )
                    )
                    style.addLayer(
                        CircleLayer(SELECTED_LAYER, SELECTED_SOURCE).withProperties(
                            circleRadius(11f), circleColor("#34D399"),
                            circleStrokeWidth(3f), circleStrokeColor("#F8FAFC")
                        )
                    )
                    styleLoaded = true
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val lifecycle = lifecycleOwner?.lifecycle
        if (lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED) == true) mapView.onStart()
        if (lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) mapView.onResume()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycle?.addObserver(observer)
        onDispose {
            lifecycle?.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(locations, riskyLocationNames, selectedLocation, map, styleLoaded) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect
        val positioned = locations.filter { it.coordinates != null }
        val normal = positioned.filterNot { it.name.toLocationRiskKey() in riskyLocationNames }
        val risky = positioned.filter { it.name.toLocationRiskKey() in riskyLocationNames }
        val selectedIsRisky = selectedLocation?.name
            ?.toLocationRiskKey()
            ?.let { it in riskyLocationNames } == true

        readyMap.style?.getSourceAs<GeoJsonSource>(NORMAL_SOURCE)?.setGeoJson(normal.toFeatures())
        readyMap.style?.getSourceAs<GeoJsonSource>(RISK_SOURCE)?.setGeoJson(risky.toFeatures())
        readyMap.style?.getSourceAs<GeoJsonSource>(SELECTED_SOURCE)
            ?.setGeoJson(selectedLocation?.let(::singleFeature) ?: emptyFeatures())
        readyMap.style?.getLayerAs<CircleLayer>(SELECTED_LAYER)?.setProperties(
            circleColor(if (selectedIsRisky) "#EF4444" else "#34D399")
        )

        val selectedCoordinates = selectedLocation?.coordinates
        if (selectedCoordinates != null) {
            readyMap.cameraPosition = CameraPosition.Builder()
                .target(LatLng(selectedCoordinates.latitude, selectedCoordinates.longitude))
                .zoom(11.0)
                .build()
        } else if (positioned.isNotEmpty()) {
            readyMap.cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        positioned.mapNotNull { it.coordinates?.latitude }.average(),
                        positioned.mapNotNull { it.coordinates?.longitude }.average()
                    )
                )
                .zoom(8.0)
                .build()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cyan: Monitored",
                style = MaterialTheme.typography.labelSmall,
                color = RiverPrimary
            )
            Text(
                text = "Rose: Flagged",
                style = MaterialTheme.typography.labelSmall,
                color = RiverDanger
            )
            Text(
                text = "Emerald: Selected",
                style = MaterialTheme.typography.labelSmall,
                color = RiverSuccess
            )
        }
        Text(
            text = "Map data © OpenStreetMap contributors · OpenFreeMap",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun List<MonitoringLocation>.toFeatures(): FeatureCollection =
    FeatureCollection.fromFeatures(mapNotNull { location ->
        val coordinates = location.coordinates ?: return@mapNotNull null
        Feature.fromGeometry(Point.fromLngLat(coordinates.longitude, coordinates.latitude))
    })

private fun singleFeature(location: MonitoringLocation): FeatureCollection {
    val coordinates = location.coordinates ?: return emptyFeatures()
    return FeatureCollection.fromFeature(
        Feature.fromGeometry(Point.fromLngLat(coordinates.longitude, coordinates.latitude))
    )
}

private fun emptyFeatures(): FeatureCollection = FeatureCollection.fromFeatures(emptyList())

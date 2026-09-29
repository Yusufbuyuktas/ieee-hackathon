package com.hackathon_ieee.myapplication.feature.report.presentation.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import org.maplibre.geojson.Point
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

private const val CURRENT_LOCATION_SOURCE_ID = "current-location-source"
private const val CURRENT_LOCATION_LAYER_ID = "current-location-layer"

@Composable
fun LocationMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()

    var mapLibreMap by remember {
        mutableStateOf<MapLibreMap?>(null)
    }

    var styleLoaded by remember {
        mutableStateOf(false)
    }

    val mapView = remember {
        MapLibre.getInstance(context)

        MapView(context).apply {
            isFocusable = false
            isFocusableInTouchMode = false
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            onCreate(null)
            getMapAsync { readyMap ->
                mapLibreMap = readyMap
                readyMap.setMaxZoomPreference(18.0)
                readyMap.setStyle(OpenFreeMapStyleUrl) { style ->
                    style.addSource(
                        GeoJsonSource(
                            CURRENT_LOCATION_SOURCE_ID,
                            Point.fromLngLat(longitude, latitude)
                        )
                    )
                    style.addLayer(
                        CircleLayer(
                            CURRENT_LOCATION_LAYER_ID,
                            CURRENT_LOCATION_SOURCE_ID
                        ).withProperties(
                            circleRadius(8f),
                            circleColor("#22D3EE"),
                            circleStrokeWidth(3f),
                            circleStrokeColor("#F8FAFC")
                        )
                    )
                    styleLoaded = true
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val lifecycle = lifecycleOwner?.lifecycle

        if (lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED) == true) {
            mapView.onStart()
        }

        if (lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
            mapView.onResume()
        }

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

    LaunchedEffect(latitude, longitude, mapLibreMap, styleLoaded) {
        val map = mapLibreMap ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect

        val currentPosition = LatLng(latitude, longitude)

        map.style
            ?.getSourceAs<GeoJsonSource>(CURRENT_LOCATION_SOURCE_ID)
            ?.setGeoJson(Point.fromLngLat(longitude, latitude))

        map.cameraPosition = CameraPosition.Builder()
            .target(currentPosition)
            .zoom(15.0)
            .build()
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Map data © OpenStreetMap contributors · OpenFreeMap",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

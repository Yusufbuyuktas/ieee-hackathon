package com.hackathon_ieee.myapplication.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double
)

class LocationProvider(
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val locationClient = LocationServices.getFusedLocationProviderClient(
        applicationContext
    )

    fun hasLocationPermission(): Boolean {
        return hasFineLocationPermission() || hasCoarseLocationPermission()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (DeviceLocation) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!hasLocationPermission()) {
            onError("Location permission is required.")
            return
        }

        val priority = if (hasFineLocationPermission()) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val cancellationTokenSource = CancellationTokenSource()

        locationClient
            .getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location == null) {
                    onError("Location is unavailable. Make sure GPS is enabled and try again.")
                } else {
                    onSuccess(
                        DeviceLocation(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
            }
            .addOnFailureListener { error ->
                onError(
                    error.localizedMessage
                        ?: "Location could not be retrieved. Please try again."
                )
            }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCoarseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

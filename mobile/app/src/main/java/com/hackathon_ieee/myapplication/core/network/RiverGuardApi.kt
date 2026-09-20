package com.hackathon_ieee.myapplication.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val DEFAULT_API_BASE_URL = "http://10.0.2.2:8080"

class RiverGuardApi(
    private val baseUrl: String = DEFAULT_API_BASE_URL
) {
    suspend fun getLocations(): Result<List<MonitoringLocation>> = runCatching {
        val response = getJsonObject("/api/locations")
        response.getJSONArray("locations").mapObjects { item ->
            MonitoringLocation(
                name = item.getString("location_name"),
                stationNo = item.nullableInt("station_no"),
                sampleTypes = item.optJSONArray("sample_types").toStringList(),
                coordinates = item.optJSONObject("coordinates").toCoordinates()
            )
        }
    }

    suspend fun getObservations(): Result<List<Observation>> = runCatching {
        val response = getJsonObject("/api/observations")
        response.getJSONArray("results").mapObjects { item ->
            Observation(
                id = item.getString("id"),
                locationName = item.getString("location_name"),
                coordinates = item.optJSONObject("coordinates").toCoordinates(),
                timestamp = item.optString("timestamp"),
                parameter = item.optString("parameter"),
                value = item.nullableDouble("value"),
                unit = item.optString("unit"),
                belowDetectionLimit = item.optBoolean("below_detection_limit"),
                sampleType = item.optString("sample_type"),
                sourceType = item.optString("source_type"),
                riskFlagged = item.optBoolean("risk_flagged")
            )
        }
    }

    suspend fun getRiskStatus(locationName: String): Result<RiskStatus> = runCatching {
        val encodedLocation = URLEncoder.encode(
            locationName,
            StandardCharsets.UTF_8.toString()
        )
        val item = getJsonObject("/api/risk-status?location=$encodedLocation")
        RiskStatus(
            location = item.optString("location"),
            riskLevel = item.optString("current_risk_level"),
            parameter = item.optString("parameter"),
            value = item.nullableDouble("value"),
            unit = item.optString("unit"),
            threshold = item.nullableDouble("threshold"),
            standard = item.optString("standard"),
            reason = item.optString("reason"),
            lastUpdated = item.optString("last_updated")
        )
    }

    suspend fun getRiskAssessments(): Result<List<RiskAssessment>> = runCatching {
        val response = getJsonObject("/api/risk-assessments")
        response.getJSONArray("results").mapObjects { item ->
            RiskAssessment(
                id = item.getString("id"),
                locationName = item.getString("location_name"),
                stationNo = item.nullableInt("station_no"),
                timestamp = item.optString("timestamp"),
                carcinogenicRisk = item.optJSONObject("carcinogenic_risk").toRiskValues(),
                totalHazardIndex = item.optJSONObject("total_hazard_index").toRiskValues(),
                riskLevel = item.optString("risk_level"),
                sourceConcludedHighRisk = item.optBoolean("source_concluded_high_risk"),
                basisNote = item.nullableString("basis_note"),
                sourceType = item.optString("source_type"),
                citation = item.nullableString("citation"),
                fhirRiskAssessmentId = item.nullableString("fhir_risk_assessment_id")
            )
        }
    }

    private suspend fun getJsonObject(path: String): JSONObject = withContext(Dispatchers.IO) {
        val connection = URL(baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            val responseBody = (
                if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }
            )?.bufferedReader()?.use { reader -> reader.readText() }.orEmpty()

            if (responseCode !in 200..299) {
                throw ApiException(
                    statusCode = responseCode,
                    message = responseBody.ifBlank { "Request failed with HTTP $responseCode." }
                )
            }

            JSONObject(responseBody)
        } finally {
            connection.disconnect()
        }
    }
}

class ApiException(
    val statusCode: Int,
    message: String
) : Exception(message)

private inline fun <T> JSONArray.mapObjects(
    transform: (JSONObject) -> T
): List<T> = buildList {
    for (index in 0 until length()) {
        add(transform(getJSONObject(index)))
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            add(optString(index))
        }
    }
}

private fun JSONObject?.toCoordinates(): ApiCoordinates? {
    if (this == null) return null
    if (!has("lat") || !has("lon") || isNull("lat") || isNull("lon")) return null
    return ApiCoordinates(
        latitude = getDouble("lat"),
        longitude = getDouble("lon")
    )
}

private fun JSONObject?.toRiskValues(): RiskValues? {
    if (this == null) return null
    return RiskValues(
        child = nullableDouble("child"),
        adult = nullableDouble("adult")
    )
}

private fun JSONObject.nullableDouble(name: String): Double? =
    if (has(name) && !isNull(name)) getDouble(name) else null

private fun JSONObject.nullableInt(name: String): Int? =
    if (has(name) && !isNull(name)) getInt(name) else null

private fun JSONObject.nullableString(name: String): String? =
    if (has(name) && !isNull(name)) getString(name) else null

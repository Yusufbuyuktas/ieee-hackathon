package com.hackathon_ieee.myapplication.core.network

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.hackathon_ieee.myapplication.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class RiverGuardApi(
    context: Context,
    private val baseUrl: String = BuildConfig.API_BASE_URL
) {
    private val sessionPreferences = context.applicationContext.getSharedPreferences(
        "riverguard_auth_session",
        Context.MODE_PRIVATE
    )

    @Volatile
    private var sessionCookie: String? = sessionPreferences.getString(SESSION_COOKIE_KEY, null)

    val hasSavedSession: Boolean
        get() = !sessionCookie.isNullOrBlank()

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<AuthUser> = runCatching {
        postJsonObject(
            path = "/api/auth/register",
            payload = JSONObject().apply {
                put("full_name", fullName.trim())
                put("email", email.trim())
                put("password", password)
            }
        )

        loginRequest(email = email, password = password)
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthUser> = runCatching {
        loginRequest(email = email, password = password)
    }

    suspend fun getCurrentUser(): Result<AuthUser> = runCatching {
        getJsonObject("/api/auth/me").toAuthUser()
    }.onFailure { error ->
        if (error is ApiException && error.statusCode in setOf(401, 403)) {
            clearSession()
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        try {
            postJsonObject(
                path = "/api/auth/logout",
                payload = JSONObject()
            )
        } finally {
            clearSession()
        }
        Unit
    }

    private suspend fun loginRequest(
        email: String,
        password: String
    ): AuthUser {
        val response = postJsonObject(
            path = "/api/auth/login",
            payload = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            },
            captureSession = true
        )
        return response.toAuthUser()
    }

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

    suspend fun getCitizenReports(): Result<List<CitizenReport>> = runCatching {
        val response = getJsonObject("/api/citizen-reports/my")
        response.getJSONArray("results").mapObjects { item ->
            val validationStatus = item.optString("ai_validation_status")
            CitizenReport(
                id = item.getString("id"),
                photoUrl = item.nullableString("photo_url")?.toAbsoluteUrl(),
                category = item.optString("category"),
                note = item.optString("note"),
                latitude = item.nullableDouble("latitude"),
                longitude = item.nullableDouble("longitude"),
                timestamp = item.optString("timestamp"),
                aiValidationStatus = validationStatus,
                aiMatchScore = item.toMatchScore(),
                aiExplanation = item.nullableString("ai_explanation"),
                fhirObservationId = item.nullableString("fhir_observation_id")
            )
        }
    }

    suspend fun submitCitizenReport(
        contentResolver: ContentResolver,
        photoUri: Uri,
        category: String,
        note: String,
        latitude: Double?,
        longitude: Double?,
        timestamp: String
    ): Result<CitizenReportSubmission> = runCatching {
        postCitizenReport(
            contentResolver = contentResolver,
            photoUri = photoUri,
            category = category,
            note = note,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp
        )
    }

    private suspend fun postCitizenReport(
        contentResolver: ContentResolver,
        photoUri: Uri,
        category: String,
        note: String,
        latitude: Double?,
        longitude: Double?,
        timestamp: String
    ): CitizenReportSubmission = withContext(Dispatchers.IO) {
        val boundary = "RiverGuardBoundary${System.currentTimeMillis()}"
        val connection = URL(
            baseUrl.trimEnd('/') + "/api/citizen-reports"
        ).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 15_000
            connection.readTimeout = 120_000
            connection.setChunkedStreamingMode(0)
            connection.setRequestProperty("Accept", "application/json")
            applySessionCookie(connection)
            connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=$boundary"
            )

            DataOutputStream(connection.outputStream).use { output ->
                output.writeTextPart(boundary, "category", category)
                output.writeTextPart(boundary, "note", note)
                latitude?.let { output.writeTextPart(boundary, "latitude", it.toString()) }
                longitude?.let { output.writeTextPart(boundary, "longitude", it.toString()) }
                output.writeTextPart(boundary, "timestamp", timestamp)

                val preparedPhoto = contentResolver.preparePhotoUpload(photoUri)

                output.writeUtf8("--$boundary\r\n")
                output.writeUtf8(
                    "Content-Disposition: form-data; name=\"photo\"; " +
                        "filename=\"${preparedPhoto.fileName}\"\r\n"
                )
                output.writeUtf8("Content-Type: ${preparedPhoto.contentType}\r\n\r\n")
                output.write(preparedPhoto.bytes)
                output.writeUtf8("\r\n--$boundary--\r\n")
                output.flush()
            }

            val responseCode = connection.responseCode
            val responseBody = (
                if (responseCode in 200..299) connection.inputStream else connection.errorStream
            )?.bufferedReader()?.use { it.readText() }.orEmpty()

            if (responseCode !in 200..299) {
                throw ApiException(
                    statusCode = responseCode,
                    message = responseBody.ifBlank {
                        "Submission failed with HTTP $responseCode."
                    }
                )
            }

            val response = JSONObject(responseBody)
            val validationStatus = response.getString("ai_validation_status")

            CitizenReportSubmission(
                id = response.getString("id"),
                aiValidationStatus = validationStatus,
                aiMatchScore = response.toMatchScore()
            )
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun getJsonObject(path: String): JSONObject = withContext(Dispatchers.IO) {
        val connection = URL(baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.setRequestProperty("Accept", "application/json")
            applySessionCookie(connection)

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

    private suspend fun postJsonObject(
        path: String,
        payload: JSONObject,
        captureSession: Boolean = false
    ): JSONObject = withContext(Dispatchers.IO) {
        val connection = URL(baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection

        try {
            val requestBytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.setFixedLengthStreamingMode(requestBytes.size)
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            applySessionCookie(connection)

            connection.outputStream.use { output ->
                output.write(requestBytes)
            }

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
                    message = responseBody.toApiErrorMessage(
                        fallback = "Request failed with HTTP $responseCode."
                    )
                )
            }

            if (captureSession) {
                val cookie = connection.headerFields.entries
                    .firstOrNull { (name, _) -> name?.equals("Set-Cookie", ignoreCase = true) == true }
                    ?.value
                    ?.firstOrNull()
                    ?.substringBefore(';')
                    ?.takeIf { it.isNotBlank() }
                    ?: throw ApiException(
                        statusCode = responseCode,
                        message = "The server did not create a login session."
                    )
                sessionCookie = cookie
                sessionPreferences.edit().putString(SESSION_COOKIE_KEY, cookie).apply()
            }

            JSONObject(responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private fun applySessionCookie(connection: HttpURLConnection) {
        sessionCookie?.let { cookie ->
            connection.setRequestProperty("Cookie", cookie)
        }
    }

    private fun clearSession() {
        sessionCookie = null
        sessionPreferences.edit().remove(SESSION_COOKIE_KEY).apply()
    }

    private companion object {
        const val SESSION_COOKIE_KEY = "session_cookie"
    }

    private fun String.toAbsoluteUrl(): String = when {
        startsWith("http://") || startsWith("https://") -> this
        startsWith("/") -> baseUrl.trimEnd('/') + this
        else -> baseUrl.trimEnd('/') + "/" + this
    }
}

private fun JSONObject.toAuthUser(): AuthUser = AuthUser(
    id = getString("id"),
    fullName = optString("fullName").ifBlank { getString("full_name") },
    email = getString("email"),
    role = getString("role")
)

private fun String.toApiErrorMessage(fallback: String): String {
    if (isBlank()) return fallback
    return runCatching {
        val response = JSONObject(this)
        response.optString("detail").ifBlank {
            response.optString("message").ifBlank {
                response.optString("error").ifBlank { fallback }
            }
        }
    }.getOrDefault(fallback)
}

private fun DataOutputStream.writeTextPart(
    boundary: String,
    name: String,
    value: String
) {
    writeUtf8("--$boundary\r\n")
    writeUtf8("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
    writeUtf8("$value\r\n")
}

private fun DataOutputStream.writeUtf8(value: String) {
    write(value.toByteArray(StandardCharsets.UTF_8))
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

private fun JSONObject.toMatchScore(): Double? {
    nullableDouble("confidence_score")?.let { return it }
    nullableDouble("ai_match_score")?.let { return it }
    nullableDouble("aiMatchScore")?.let { return it }
    return nullableDouble("ai_confidence")
}

package com.hackathon_ieee.myapplication.core.storage

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedCitizenReport(
    val id: String,
    val ownerEmail: String,
    val category: String,
    val note: String,
    val latitude: Double,
    val longitude: Double,
    val submittedAtMillis: Long,
    val aiValidationStatus: String,
    val aiMatchScore: Double?,
    val photoUrl: String? = null
)

class LocalReportRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "riverguard_local_reports",
        Context.MODE_PRIVATE
    )

    fun getReports(ownerEmail: String): List<SavedCitizenReport> =
        readAll()
            .filter { it.ownerEmail.equals(ownerEmail, ignoreCase = true) }
            .sortedByDescending { it.submittedAtMillis }

    fun save(report: SavedCitizenReport) {
        val reports = readAll().toMutableList()
        reports.removeAll { it.id == report.id }
        reports.add(report)

        writeAll(reports)
    }

    fun replaceForOwner(ownerEmail: String, reports: List<SavedCitizenReport>) {
        val retainedReports = readAll().filterNot {
            it.ownerEmail.equals(ownerEmail, ignoreCase = true)
        }
        writeAll(retainedReports + reports)
    }

    private fun writeAll(reports: List<SavedCitizenReport>) {
        val json = JSONArray()
        reports.forEach { item ->
            json.put(
                JSONObject()
                    .put("id", item.id)
                    .put("owner_email", item.ownerEmail)
                    .put("category", item.category)
                    .put("note", item.note)
                    .put("latitude", item.latitude)
                    .put("longitude", item.longitude)
                    .put("submitted_at_millis", item.submittedAtMillis)
                    .put("ai_validation_status", item.aiValidationStatus)
                    .put("ai_match_score", item.aiMatchScore ?: JSONObject.NULL)
                    .put("photo_url", item.photoUrl ?: JSONObject.NULL)
            )
        }

        preferences.edit().putString(REPORTS_KEY, json.toString()).apply()
    }

    private fun readAll(): List<SavedCitizenReport> = runCatching {
        val value = preferences.getString(REPORTS_KEY, null) ?: return emptyList()
        val json = JSONArray(value)

        buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    SavedCitizenReport(
                        id = item.getString("id"),
                        ownerEmail = item.getString("owner_email"),
                        category = item.getString("category"),
                        note = item.optString("note"),
                        latitude = item.getDouble("latitude"),
                        longitude = item.getDouble("longitude"),
                        submittedAtMillis = item.getLong("submitted_at_millis"),
                        aiValidationStatus = item.getString("ai_validation_status"),
                        aiMatchScore = when {
                            item.has("ai_match_score") && !item.isNull("ai_match_score") ->
                                item.getDouble("ai_match_score")
                            item.optString("ai_validation_status") == "ONAYLANDI" &&
                                item.has("ai_confidence") && !item.isNull("ai_confidence") ->
                                item.getDouble("ai_confidence")
                            else -> null
                        },
                        photoUrl = if (
                            item.has("photo_url") && !item.isNull("photo_url")
                        ) {
                            item.getString("photo_url")
                        } else {
                            null
                        }
                    )
                )
            }
        }
    }.getOrElse { emptyList() }

    private companion object {
        const val REPORTS_KEY = "reports"
    }
}

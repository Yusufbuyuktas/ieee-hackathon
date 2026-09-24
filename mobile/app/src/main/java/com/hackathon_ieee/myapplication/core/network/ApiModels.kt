package com.hackathon_ieee.myapplication.core.network

data class ApiCoordinates(
    val latitude: Double,
    val longitude: Double
)

data class MonitoringLocation(
    val name: String,
    val stationNo: Int?,
    val sampleTypes: List<String>,
    val coordinates: ApiCoordinates?
)

data class Observation(
    val id: String,
    val locationName: String,
    val coordinates: ApiCoordinates?,
    val timestamp: String,
    val parameter: String,
    val value: Double?,
    val unit: String,
    val belowDetectionLimit: Boolean,
    val sampleType: String,
    val sourceType: String,
    val riskFlagged: Boolean
)

data class RiskStatus(
    val location: String,
    val riskLevel: String,
    val parameter: String,
    val value: Double?,
    val unit: String,
    val threshold: Double?,
    val standard: String,
    val reason: String,
    val lastUpdated: String
)

data class RiskValues(
    val child: Double?,
    val adult: Double?
)

data class RiskAssessment(
    val id: String,
    val locationName: String,
    val stationNo: Int?,
    val timestamp: String,
    val carcinogenicRisk: RiskValues?,
    val totalHazardIndex: RiskValues?,
    val riskLevel: String,
    val sourceConcludedHighRisk: Boolean,
    val basisNote: String?,
    val sourceType: String,
    val citation: String?,
    val fhirRiskAssessmentId: String?
)

data class CitizenReportSubmission(
    val id: String,
    val aiValidationStatus: String,
    val aiConfidence: Double?
)

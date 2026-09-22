package com.hackathon_ieee.myapplication.feature.report.domain.model

enum class ReportCategory(
    val apiValue: String,
    val displayName: String
) {
    TURBID_WATER(
        apiValue = "bulanik",
        displayName = "Turbid water"
    ),

    COLOR_CHANGE(
        apiValue = "kirli_renk_degisimi",
        displayName = "Water discoloration"
    ),

    FISH_DEATH(
        apiValue = "balik_olumu",
        displayName = "Fish mortality"
    ),

    BAD_SMELL(
        apiValue = "kotu_koku",
        displayName = "Bad odor"
    ),

    OTHER(
        apiValue = "diger",
        displayName = "Other"
    )
}

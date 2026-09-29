package com.hackathon_ieee.myapplication.feature.report.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import com.hackathon_ieee.myapplication.ui.theme.MobileTheme
import com.hackathon_ieee.myapplication.ui.theme.RiverSuccess
import com.hackathon_ieee.myapplication.ui.theme.RiverWarning
import java.util.Locale

@Composable
fun ReportStatusScreen(
    reportId: String,
    aiValidationStatus: String,
    aiMatchScore: Double?,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presentation = aiValidationStatus.toStatusPresentation()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = presentation.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = presentation.color,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = presentation.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        SubtlePanel {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultRow(
                    label = "Report ID",
                    value = reportId,
                    singleLine = true
                )
                ResultRow(label = "Status", value = presentation.statusLabel)
                aiMatchScore?.let { matchScore ->
                    ResultRow(
                        label = "AI match",
                        value = String.format(
                            Locale.US,
                            "%.0f%%",
                            matchScore.coerceIn(0.0, 1.0) * 100
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    singleLine: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            softWrap = !singleLine,
            overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip
        )
    }
}

private data class StatusPresentation(
    val title: String,
    val statusLabel: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun String.toStatusPresentation(): StatusPresentation = when (this) {
    "ONAYLANDI" -> StatusPresentation(
        title = "Report Submitted",
        statusLabel = "Approved",
        description = "Your observation was received and its AI matched the selected category.",
        color = RiverSuccess
    )
    "INCELEMEDE" -> StatusPresentation(
        title = "Report Under Review",
        statusLabel = "Under review",
        description = "Your observation was received and saved for further review.",
        color = RiverWarning
    )
    "AI_SERVISI_ERISILEMEDI" -> StatusPresentation(
        title = "Report Submitted",
        statusLabel = "AI evaluation unavailable",
        description = "Your observation was saved successfully, but automated evaluation is temporarily unavailable.",
        color = RiverWarning
    )
    else -> StatusPresentation(
        title = "Report Submitted",
        statusLabel = replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
        description = "Your observation was received successfully.",
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(
    name = "Approved Report Status",
    showBackground = true,
    backgroundColor = 0xFF020617,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ReportStatusScreenPreview() {
    MobileTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ReportStatusScreen(
                reportId = "cit-8f73b11d",
                aiValidationStatus = "ONAYLANDI",
                aiMatchScore = 0.87,
                onBackHome = {}
            )
        }
    }
}

@Preview(
    name = "Report Status Under Review",
    showBackground = true,
    backgroundColor = 0xFF020617,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ReportStatusUnderReviewPreview() {
    MobileTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ReportStatusScreen(
                reportId = "cit-42c90ab1",
                aiValidationStatus = "INCELEMEDE",
                aiMatchScore = 0.64,
                onBackHome = {}
            )
        }
    }
}

@Preview(
    name = "Report Status AI Unavailable",
    showBackground = true,
    backgroundColor = 0xFF020617,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ReportStatusAiUnavailablePreview() {
    MobileTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ReportStatusScreen(
                reportId = "cit-71de903f",
                aiValidationStatus = "AI_SERVISI_ERISILEMEDI",
                aiMatchScore = null,
                onBackHome = {}
            )
        }
    }
}

package com.hackathon_ieee.myapplication.feature.profile

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hackathon_ieee.myapplication.core.storage.SavedCitizenReport
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import com.hackathon_ieee.myapplication.ui.theme.RiverDanger
import com.hackathon_ieee.myapplication.ui.theme.RiverSuccess
import com.hackathon_ieee.myapplication.ui.theme.RiverWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    fullName: String,
    email: String,
    role: String,
    reports: List<SavedCitizenReport>,
    isRefreshing: Boolean,
    refreshMessage: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReport by remember { mutableStateOf<SavedCitizenReport?>(null) }
    val reportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = email.firstOrNull()?.uppercase() ?: "R",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = fullName.ifBlank { "RiverGuard Explorer" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = role.toRoleLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "My Reports",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = androidx.compose.ui.graphics.Color.White
                )

                refreshMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = RiverWarning
                    )
                }

                if (reports.isEmpty()) {
                    SubtlePanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "No submitted reports yet.", fontSize = 15.sp)
                            Text(
                                text = "Reports submitted from this device will appear here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    reports.forEach { report ->
                        ReportListItem(
                            report = report,
                            onClick = { selectedReport = report }
                        )
                    }
                }
            }

            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Log Out")
            }
        }
    }

    selectedReport?.let { report ->
        ModalBottomSheet(
            onDismissRequest = { selectedReport = null },
            sheetState = reportSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ReportDetails(
                report = report,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun ReportListItem(
    report: SavedCitizenReport,
    onClick: () -> Unit
) {
    val presentation = report.aiValidationStatus.toReportStatus()
    SubtlePanel(modifier = Modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = presentation.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = presentation.color
                )
            }
            Text(
                text = formatReportDate(report.submittedAtMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap to view report details",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReportDetails(
    report: SavedCitizenReport,
    modifier: Modifier = Modifier
) {
    val presentation = report.aiValidationStatus.toReportStatus()
    var photoLoadFailed by remember(report.photoUrl) { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        report.photoUrl?.takeUnless { photoLoadFailed }?.let { photoUrl ->
            AsyncImage(
                model = photoUrl,
                contentDescription = "Submitted report photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                onError = { photoLoadFailed = true }
            )
        }
        Text(
            text = report.category,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        DetailField("Status", presentation.label, presentation.color)
        report.aiMatchScore?.let {
            DetailField("Photo match", formatMatchScore(it))
        }
        DetailField("Submitted", formatReportDate(report.submittedAtMillis))
        DetailField("Report ID", report.id)
        DetailField(
            "Location",
            String.format(Locale.US, "%.6f, %.6f", report.latitude, report.longitude)
        )
        DetailField("Note", report.note.ifBlank { "No additional note." })
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (report.photoUrl == null) {
                "Stored locally on this device"
            } else {
                "Latest details synced with RiverGuard"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
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
            color = valueColor
        )
    }
}

private data class ReportStatusPresentation(
    val label: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun String.toReportStatus(): ReportStatusPresentation = when (this) {
    "ONAYLANDI" -> ReportStatusPresentation("Approved", RiverSuccess)
    "INCELEMEDE" -> ReportStatusPresentation("Under review", RiverWarning)
    "TUTARSIZ" -> ReportStatusPresentation("Not verified", RiverDanger)
    "AI_SERVISI_ERISILEMEDI" -> ReportStatusPresentation("AI unavailable", RiverWarning)
    else -> ReportStatusPresentation(
        replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
        MaterialTheme.colorScheme.primary
    )
}

private fun formatReportDate(timestamp: Long): String =
    SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.US).format(Date(timestamp))

private fun formatMatchScore(matchScore: Double): String =
    String.format(Locale.US, "%.0f%% match", matchScore.coerceIn(0.0, 1.0) * 100)

private fun String.toRoleLabel(): String = when (this) {
    "CITIZEN" -> "Citizen"
    "DOCTOR" -> "Doctor"
    "MUNICIPALITY_STAFF" -> "Municipality staff"
    else -> replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
}

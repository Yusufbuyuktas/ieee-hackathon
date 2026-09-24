package com.hackathon_ieee.myapplication.feature.report.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hackathon_ieee.myapplication.core.network.ApiException
import com.hackathon_ieee.myapplication.core.network.CitizenReportSubmission
import com.hackathon_ieee.myapplication.core.network.RiverGuardApi
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory
import com.hackathon_ieee.myapplication.feature.report.presentation.components.LocationMap
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportReviewScreen(
    photoUri: String,
    category: ReportCategory,
    latitude: Double,
    longitude: Double,
    note: String,
    onEdit: () -> Unit,
    onSubmitted: (CitizenReportSubmission) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val api = remember { RiverGuardApi() }
    val coroutineScope = rememberCoroutineScope()
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    var submissionError by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Check the details before submitting your report.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ReviewCard(title = "Photo") {
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = "Water observation photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
        }

        ReviewCard(title = "Category") {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        ReviewCard(title = "Location") {
            LocationMap(
                latitude = latitude,
                longitude = longitude
            )
        }

        ReviewCard(title = "Additional note") {
            Text(
                text = note.ifBlank { "No additional note." },
                style = MaterialTheme.typography.bodyLarge,
                color = if (note.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        OutlinedButton(
            onClick = onEdit,
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit Report")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (isSubmitting) return@Button
                    isSubmitting = true
                    submissionError = null
                    coroutineScope.launch {
                        val timestamp = SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ssXXX",
                            Locale.US
                        ).format(Date())

                        api.submitCitizenReport(
                            contentResolver = context.contentResolver,
                            photoUri = Uri.parse(photoUri),
                            category = category.apiValue,
                            note = note,
                            latitude = latitude,
                            longitude = longitude,
                            timestamp = timestamp
                        ).onSuccess(onSubmitted).onFailure { error ->
                            submissionError = error.toSubmissionMessage()
                            isSubmitting = false
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "  Submitting Report…"
                    )
                } else {
                    Text(text = "Submit Report")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSubmitting) {
                    "The photo is being securely uploaded and evaluated."
                } else {
                    "Your report will be evaluated after submission."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            submissionError?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

private fun Throwable.toSubmissionMessage(): String = when {
    this is ApiException && statusCode == 400 ->
        "The report details were not accepted. Check the photo and form fields, then try again."
    this is ApiException && statusCode == 413 ->
        "The selected photo is too large. Choose a smaller photo and try again."
    this is ApiException && statusCode in 500..599 ->
        "The report service is temporarily unavailable. Please try again shortly."
    else ->
        "The report could not be submitted. Check your connection and try again."
}

@Composable
private fun ReviewCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        SubtlePanel {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

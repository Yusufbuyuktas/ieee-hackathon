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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory
import com.hackathon_ieee.myapplication.feature.report.presentation.components.LocationMap
import com.hackathon_ieee.myapplication.ui.components.GradientPanel

@Composable
fun ReportReviewScreen(
    photoUri: String,
    category: ReportCategory,
    latitude: Double,
    longitude: Double,
    note: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit Report")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Submit Report")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Submission will be enabled when the report API is available.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
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

        GradientPanel {
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

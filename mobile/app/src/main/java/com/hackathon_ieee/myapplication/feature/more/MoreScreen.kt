package com.hackathon_ieee.myapplication.feature.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.ui.components.GradientPanel

@Composable
fun MoreScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InformationCard(
            title = "How reporting works",
            body = "Add a water observation photo, select a category, capture the location, and review the details before submission."
        )

        InformationCard(
            title = "Photo and location",
            body = "Photos document visible conditions. Location data connects each observation to the correct monitoring area."
        )

        GradientPanel {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About RiverGuard",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "RiverGuard is a citizen-supported water quality monitoring application designed for the Ergene Basin. It helps communities document environmental observations and make water risks easier to understand.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "International competition project",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InformationCard(
    title: String,
    body: String
) {
    GradientPanel {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

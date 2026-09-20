package com.hackathon_ieee.myapplication.feature.more

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.R

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
        Text(
            text = "How reporting works",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Turn something unusual into a useful observation in four quick steps.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ReportingStep(
                number = "1",
                title = "Spot",
                description = "Notice a visible change or concern in the water."
            )
            ReportingStep(
                number = "2",
                title = "Capture",
                description = "Add a clear photo and select what you observed."
            )
            ReportingStep(
                number = "3",
                title = "Pin",
                description = "Confirm the location so the report has context."
            )
            ReportingStep(
                number = "4",
                title = "Review",
                description = "Check the details and prepare the observation for submission."
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "About RiverGuard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "RiverGuard is a citizen-supported water quality monitoring application focused on the Ergene Basin. It brings environmental data and local observations together, making changes in water conditions easier to document and understand. By helping communities report concerns with clear evidence and location information, RiverGuard aims to support earlier awareness and more informed action for healthier rivers and surrounding ecosystems.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Meet the Team",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Column {
                TeamMemberRow(
                    name = "Serranur Türkoğlu",
                    role = "Mobile Development",
                    githubUrl = "https://github.com/serra888"
                )
                TeamDivider()
                TeamMemberRow(
                    name = "Ahmet Hilmi Güler",
                    role = "Backend & FHIR/Data",
                    githubUrl = "https://github.com/ahilmii"
                )
                TeamDivider()
                TeamMemberRow(
                    name = "Yusuf Büyüktaş",
                    role = "Web Development",
                    githubUrl = "https://github.com/Yusufbuyuktas"
                )
                TeamDivider()
                TeamMemberRow(
                    name = "Faruk Turnalı",
                    role = "AI Development",
                    githubUrl = "https://github.com/farukk06"
                )
        }

        Text(
            text = "Version 1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReportingStep(
    number: String,
    title: String,
    description: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TeamDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    )
}

@Composable
private fun TeamMemberRow(
    name: String,
    role: String,
    githubUrl: String
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {
                uriHandler.openUri(githubUrl)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = "Open $name's GitHub profile",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

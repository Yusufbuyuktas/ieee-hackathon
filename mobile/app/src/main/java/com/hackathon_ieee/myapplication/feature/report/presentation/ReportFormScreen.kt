package com.hackathon_ieee.myapplication.feature.report.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.feature.report.domain.model.ReportCategory

@Composable
fun ReportFormScreen(
    modifier: Modifier = Modifier
) {
    var selectedCategoryName by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var note by rememberSaveable {
        mutableStateOf("")
    }

    val selectedCategory = selectedCategoryName?.let { categoryName ->
        ReportCategory.valueOf(categoryName)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Share information about the water pollution you observed."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Photo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No photo selected"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = {
                        // Camera and gallery support will be added later.
                    }
                ) {
                    Text(
                        text = "Add Photo"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ReportCategory.entries.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCategoryName = category.name
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategoryName = category.name
                        }
                    )

                    Text(
                        text = category.displayName,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Additional note",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = { newValue ->
                if (newValue.length <= 500) {
                    note = newValue
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            label = {
                Text(
                    text = "Describe your observation"
                )
            },
            supportingText = {
                Text(
                    text = "${note.length}/500"
                )
            },
            minLines = 4
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Your location will be added automatically using GPS.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                // Review screen will be added later.
            },
            enabled = selectedCategory != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Continue"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

package com.hackathon_ieee.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

@Composable
fun GradientPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.primary.copy(alpha = 0f),
                        0.22f to MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        0.68f to MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        1f to MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                    )
                )
            ),
        content = content
    )
}

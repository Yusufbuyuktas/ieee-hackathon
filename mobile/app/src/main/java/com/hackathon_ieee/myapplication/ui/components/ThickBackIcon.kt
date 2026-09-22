package com.hackathon_ieee.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun ThickBackIcon(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) {
    Canvas(
        modifier = modifier
            .size(24.dp)
            .semantics {
                contentDescription = "Go back"
            }
    ) {
        val strokeWidth = 3.dp.toPx()
        val startX = size.width * 0.2f
        val endX = size.width * 0.84f
        val centerY = size.height * 0.5f

        drawLine(
            color = color,
            start = Offset(startX, centerY),
            end = Offset(endX, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(startX, centerY),
            end = Offset(size.width * 0.46f, size.height * 0.22f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(startX, centerY),
            end = Offset(size.width * 0.46f, size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

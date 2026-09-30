package com.hackathon_ieee.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PasswordVisibilityIcon(
    passwordVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) {
    Canvas(
        modifier = modifier
            .size(24.dp)
            .semantics {
                contentDescription = if (passwordVisible) {
                    "Hide password"
                } else {
                    "Show password"
                }
            }
    ) {
        val strokeWidth = 1.8.dp.toPx()
        val eyePath = Path().apply {
            moveTo(size.width * 0.08f, size.height * 0.5f)
            cubicTo(
                size.width * 0.28f,
                size.height * 0.2f,
                size.width * 0.72f,
                size.height * 0.2f,
                size.width * 0.92f,
                size.height * 0.5f
            )
            cubicTo(
                size.width * 0.72f,
                size.height * 0.8f,
                size.width * 0.28f,
                size.height * 0.8f,
                size.width * 0.08f,
                size.height * 0.5f
            )
        }

        drawPath(
            path = eyePath,
            color = color,
            style = Stroke(width = strokeWidth)
        )
        drawOval(
            color = color,
            topLeft = Offset(size.width * 0.4f, size.height * 0.4f),
            size = Size(size.width * 0.2f, size.height * 0.2f)
        )

        if (passwordVisible) {
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, size.height * 0.16f),
                end = Offset(size.width * 0.82f, size.height * 0.84f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

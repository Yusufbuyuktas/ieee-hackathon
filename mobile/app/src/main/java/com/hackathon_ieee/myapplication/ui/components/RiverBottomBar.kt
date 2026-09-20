package com.hackathon_ieee.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

enum class BottomDestination {
    HOME,
    MAP,
    REPORT,
    MY_REPORTS,
    MORE
}

@Composable
fun RiverBottomBar(
    selectedDestination: BottomDestination,
    onDestinationSelected: (BottomDestination) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 15.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            BottomBarItem(
                destination = BottomDestination.HOME,
                selectedDestination = selectedDestination,
                contentDescription = "Home",
                onDestinationSelected = onDestinationSelected
            )
            BottomBarItem(
                destination = BottomDestination.MAP,
                selectedDestination = selectedDestination,
                contentDescription = "Risk map",
                onDestinationSelected = onDestinationSelected
            )
            BottomBarItem(
                destination = BottomDestination.REPORT,
                selectedDestination = selectedDestination,
                contentDescription = "Create report",
                emphasized = true,
                onDestinationSelected = onDestinationSelected
            )
            BottomBarItem(
                destination = BottomDestination.MY_REPORTS,
                selectedDestination = selectedDestination,
                contentDescription = "My reports",
                onDestinationSelected = onDestinationSelected
            )
            BottomBarItem(
                destination = BottomDestination.MORE,
                selectedDestination = selectedDestination,
                contentDescription = "More",
                onDestinationSelected = onDestinationSelected
            )
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    destination: BottomDestination,
    selectedDestination: BottomDestination,
    contentDescription: String,
    onDestinationSelected: (BottomDestination) -> Unit,
    emphasized: Boolean = false
) {
    val selected = destination == selectedDestination

    NavigationBarItem(
        selected = selected,
        onClick = {
            onDestinationSelected(destination)
        },
        icon = {
            if (emphasized) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        RiverNavigationIcon(
                            destination = destination,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            } else {
                RiverNavigationIcon(
                    destination = destination,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        alwaysShowLabel = false,
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = if (emphasized) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        modifier = Modifier.semantics {
            this.contentDescription = contentDescription
        }
    )
}

@Composable
private fun RiverNavigationIcon(
    destination: BottomDestination,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)

        when (destination) {
            BottomDestination.HOME -> {
                val house = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.48f)
                    lineTo(size.width * 0.50f, size.height * 0.18f)
                    lineTo(size.width * 0.85f, size.height * 0.48f)
                    lineTo(size.width * 0.78f, size.height * 0.48f)
                    lineTo(size.width * 0.78f, size.height * 0.82f)
                    lineTo(size.width * 0.58f, size.height * 0.82f)
                    lineTo(size.width * 0.58f, size.height * 0.60f)
                    lineTo(size.width * 0.42f, size.height * 0.60f)
                    lineTo(size.width * 0.42f, size.height * 0.82f)
                    lineTo(size.width * 0.22f, size.height * 0.82f)
                    lineTo(size.width * 0.22f, size.height * 0.48f)
                    close()
                }
                drawPath(house, color = color, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            }

            BottomDestination.MAP -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.30f,
                    center = Offset(center.x, size.height * 0.40f),
                    style = Stroke(strokeWidth)
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.08f,
                    center = Offset(center.x, size.height * 0.40f)
                )
                drawLine(
                    color = color,
                    start = Offset(center.x - size.width * 0.19f, size.height * 0.63f),
                    end = Offset(center.x, size.height * 0.88f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(center.x + size.width * 0.19f, size.height * 0.63f),
                    end = Offset(center.x, size.height * 0.88f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            BottomDestination.REPORT -> {
                drawLine(
                    color = color,
                    start = Offset(center.x, size.height * 0.20f),
                    end = Offset(center.x, size.height * 0.80f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.20f, center.y),
                    end = Offset(size.width * 0.80f, center.y),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            BottomDestination.MY_REPORTS -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.20f, size.height * 0.16f),
                    size = androidx.compose.ui.geometry.Size(
                        size.width * 0.60f,
                        size.height * 0.70f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                    style = Stroke(strokeWidth)
                )
                repeat(3) { index ->
                    val y = size.height * (0.36f + index * 0.17f)
                    drawLine(
                        color = color,
                        start = Offset(size.width * 0.34f, y),
                        end = Offset(size.width * 0.68f, y),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            BottomDestination.MORE -> {
                translate(left = center.x, top = center.y) {
                    drawCircle(color = color, radius = 2.4.dp.toPx(), center = Offset(-8.dp.toPx(), 0f))
                    drawCircle(color = color, radius = 2.4.dp.toPx(), center = Offset.Zero)
                    drawCircle(color = color, radius = 2.4.dp.toPx(), center = Offset(8.dp.toPx(), 0f))
                }
            }
        }
    }
}

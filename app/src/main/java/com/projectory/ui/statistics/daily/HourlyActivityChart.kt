package com.projectory.ui.statistics.daily

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun HourlyActivityChart(
    hourlyActivities: List<HourlyActivity>,
    modifier: Modifier = Modifier
) {
    val maxTime = hourlyActivities.maxOfOrNull { it.timeSpent } ?: 1

    Canvas(modifier = modifier) {
        val barWidth = size.width / 24
        val maxHeight = size.height - 40.dp.toPx()

        hourlyActivities.forEachIndexed { index, activity ->
            val barHeight = if (maxTime > 0) {
                (activity.timeSpent.toFloat() / maxTime) * maxHeight
            } else 0f

            // Draw bar
            drawRect(
                color = Color(0xFF6366F1),
                topLeft = Offset(index * barWidth + barWidth * 0.2f, maxHeight - barHeight),
                size = Size(barWidth * 0.6f, barHeight)
            )

            // Draw hour label
            if (index % 3 == 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${activity.hour}h",
                        index * barWidth + barWidth / 2,
                        size.height - 10.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}
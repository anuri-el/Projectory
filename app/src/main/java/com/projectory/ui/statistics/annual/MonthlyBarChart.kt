package com.projectory.ui.statistics.annual

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun MonthlyBarChart(
    monthlyData: List<MonthlyData>,
    modifier: Modifier = Modifier
) {
    val maxTime = monthlyData.maxOfOrNull { it.timeSpent } ?: 1

    Canvas(modifier = modifier) {
        val barWidth = size.width / 12
        val maxHeight = size.height - 40.dp.toPx()

        monthlyData.forEachIndexed { index, data ->
            val barHeight = if (maxTime > 0) {
                (data.timeSpent.toFloat() / maxTime) * maxHeight
            } else 0f

            // Draw bar
            drawRoundRect(
                color = Color(0xFF6366F1),
                topLeft = Offset(index * barWidth + barWidth * 0.25f, maxHeight - barHeight),
                size = Size(barWidth * 0.5f, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )

            // Draw month label
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    data.monthName,
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
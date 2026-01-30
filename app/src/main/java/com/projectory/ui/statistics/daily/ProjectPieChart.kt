package com.projectory.ui.statistics.daily

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.min

@Composable
fun ProjectPieChart(
    projects: List<ProjectTimeBreakdown>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B),
        Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF14B8A6),
        Color(0xFFF97316), Color(0xFF06B6D4), Color(0xFFEF4444)
    )

    Canvas(modifier = modifier) {
        val radius = min(size.width, size.height) / 2.5f
        val center = Offset(size.width / 2, size.height / 2)

        var startAngle = -90f

        projects.take(9).forEachIndexed { index, project ->
            val sweepAngle = (project.percentage / 100f) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }

        // Draw white circle in center for donut effect
        drawCircle(
            color = Color.White,
            radius = radius * 0.5f,
            center = center
        )
    }
}
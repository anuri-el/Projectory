package com.projectory.ui.statistics.annual

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun YearOverviewCards(
    totalProjects: Int,
    completedProjects: Int,
    activeProjects: Int,
    totalTime: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OverviewCard(
            icon = Icons.Default.Folder,
            title = "Total",
            value = totalProjects.toString(),
            color = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            icon = Icons.Default.CheckCircle,
            title = "Completed",
            value = completedProjects.toString(),
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OverviewCard(
            icon = Icons.Default.PlayArrow,
            title = "Active",
            value = activeProjects.toString(),
            color = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            icon = Icons.Default.Timer,
            title = "Time",
            value = formatHours(totalTime),
            color = Color(0xFFEC4899),
            modifier = Modifier.weight(1f)
        )
    }
}
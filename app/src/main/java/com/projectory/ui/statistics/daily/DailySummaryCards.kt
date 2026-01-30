package com.projectory.ui.statistics.daily

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DailySummaryCards(
    totalTime: Long,
    totalTasks: Int,
    totalNotes: Int,
    projectsWorkedOn: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            icon = Icons.Default.Timer,
            title = "Time",
            value = formatTimeShort(totalTime),
            color = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            icon = Icons.Default.CheckCircle,
            title = "Tasks",
            value = totalTasks.toString(),
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            icon = Icons.Default.Description,
            title = "Notes",
            value = totalNotes.toString(),
            color = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            icon = Icons.Default.Folder,
            title = "Projects",
            value = projectsWorkedOn.toString(),
            color = Color(0xFFEC4899),
            modifier = Modifier.weight(1f)
        )
    }
}
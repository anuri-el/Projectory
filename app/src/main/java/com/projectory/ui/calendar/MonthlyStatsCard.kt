package com.projectory.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonthlyStatsCard(stats: MonthlyStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MonthStatItem(
                icon = Icons.Default.CalendarMonth,
                value = "${stats.activeDays}/${stats.totalDays}",
                label = "Active Days"
            )
            MonthStatItem(
                icon = Icons.Default.Timer,
                value = formatTimeShort(stats.totalTimeSpent),
                label = "Total Time"
            )
            MonthStatItem(
                icon = Icons.Default.Folder,
                value = stats.projectsWorkedOn.toString(),
                label = "Projects"
            )
        }
    }
}
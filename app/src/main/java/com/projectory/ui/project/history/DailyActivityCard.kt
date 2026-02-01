package com.projectory.ui.project.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@Composable
fun DailyActivityCard(dailyActivity: DailyActivitySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    dailyActivity.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (dailyActivity.timeSpent > 0) {
                    ActivityStat(
                        icon = Icons.Default.Timer,
                        label = "Time",
                        value = formatTimeShort(dailyActivity.timeSpent)
                    )
                }
                if (dailyActivity.tasksCompleted > 0) {
                    ActivityStat(
                        icon = Icons.Default.CheckCircle,
                        label = "Tasks",
                        value = dailyActivity.tasksCompleted.toString()
                    )
                }
                if (dailyActivity.notesAdded > 0) {
                    ActivityStat(
                        icon = Icons.Default.Note,
                        label = "Notes",
                        value = dailyActivity.notesAdded.toString()
                    )
                }
            }
        }
    }
}

private fun formatTimeShort(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}
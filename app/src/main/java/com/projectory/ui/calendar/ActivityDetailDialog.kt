package com.projectory.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.projectory.domain.model.Activity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ActivityDetailDialog(
    date: LocalDate,
    activities: List<Activity>,
    projectsMap: Map<Long, com.projectory.domain.model.Project>,
    onDismiss: () -> Unit,
    onNavigateToProject: (Long) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val totalTime = activities.sumOf { it.timeSpent }
                        val totalTasks = activities.sumOf { it.tasksCompleted }
                        val totalNotes = activities.sumOf { it.notesAdded }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (totalTime > 0) {
                                DayStatChip(
                                    icon = Icons.Default.Timer,
                                    text = formatTimeShort(totalTime)
                                )
                            }
                            if (totalTasks > 0) {
                                DayStatChip(
                                    icon = Icons.Default.CheckCircle,
                                    text = "$totalTasks tasks"
                                )
                            }
                            if (totalNotes > 0) {
                                DayStatChip(
                                    icon = Icons.Default.Note,
                                    text = "$totalNotes notes"
                                )
                            }
                        }
                    }
                }

                // Activities list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities) { activity ->
                        val project = projectsMap[activity.projectId]
                        if (project != null) {
                            ActivityItem(
                                activity = activity,
                                project = project,
                                onClick = { onNavigateToProject(project.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.projectory.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.projectory.domain.model.ProjectStatus
import com.projectory.domain.model.Task

@Composable
fun SessionCompleteDialog(
    sessionTime: Long,
    tasks: List<Task>,
    onDismiss: () -> Unit,
    onStatusChange: (ProjectStatus) -> Unit,
    onTasksSelected: (List<Long>) -> Unit,
    onConfirm: () -> Unit
) {
    var selectedTaskIds by remember { mutableStateOf(setOf<Long>()) }
    var selectedStatus by remember { mutableStateOf<ProjectStatus?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Session Complete!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Time spent: ${formatTime(sessionTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Change Section
                    item {
                        Text(
                            "Change Project Status (Optional)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusButton(
                                status = ProjectStatus.COMPLETED,
                                icon = Icons.Default.CheckCircle,
                                isSelected = selectedStatus == ProjectStatus.COMPLETED,
                                onClick = {
                                    selectedStatus = if (selectedStatus == ProjectStatus.COMPLETED) {
                                        null
                                    } else {
                                        ProjectStatus.COMPLETED
                                    }
                                }
                            )
                            StatusButton(
                                status = ProjectStatus.PAUSED,
                                icon = Icons.Default.Pause,
                                isSelected = selectedStatus == ProjectStatus.PAUSED,
                                onClick = {
                                    selectedStatus = if (selectedStatus == ProjectStatus.PAUSED) {
                                        null
                                    } else {
                                        ProjectStatus.PAUSED
                                    }
                                }
                            )
                            StatusButton(
                                status = ProjectStatus.GAVE_UP,
                                icon = Icons.Default.Cancel,
                                isSelected = selectedStatus == ProjectStatus.GAVE_UP,
                                onClick = {
                                    selectedStatus = if (selectedStatus == ProjectStatus.GAVE_UP) {
                                        null
                                    } else {
                                        ProjectStatus.GAVE_UP
                                    }
                                }
                            )
                        }
                    }

                    // Tasks Section
                    if (tasks.isNotEmpty()) {
                        item {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Mark Tasks as Completed (Optional)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Select tasks you completed during this session",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        items(tasks.filter { !it.isCompleted }) { task ->
                            TaskCheckItem(
                                task = task,
                                isSelected = selectedTaskIds.contains(task.id),
                                onToggle = {
                                    selectedTaskIds = if (selectedTaskIds.contains(task.id)) {
                                        selectedTaskIds - task.id
                                    } else {
                                        selectedTaskIds + task.id
                                    }
                                }
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Skip")
                    }
                    Button(
                        onClick = {
                            selectedStatus?.let { onStatusChange(it) }
                            if (selectedTaskIds.isNotEmpty()) {
                                onTasksSelected(selectedTaskIds.toList())
                            }
                            onConfirm()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedStatus != null || selectedTaskIds.isNotEmpty()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}
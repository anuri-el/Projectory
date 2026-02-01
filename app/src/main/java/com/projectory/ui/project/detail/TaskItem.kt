package com.projectory.ui.project.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projectory.domain.model.Task
import java.time.format.DateTimeFormatter

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                if (task.isCompleted && task.completedDate != null) {
                    Text(
                        "Completed ${task.completedDate.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Reorder buttons
            if (!task.isCompleted) {
                Column {
                    IconButton(
                        onClick = { onMoveUp?.invoke() },
                        enabled = onMoveUp != null
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move up",
                            tint = if (onMoveUp != null)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    IconButton(
                        onClick = { onMoveDown?.invoke() },
                        enabled = onMoveDown != null
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move down",
                            tint = if (onMoveDown != null)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        EditTaskDialog(
            currentTitle = task.title,
            onDismiss = { showEditDialog = false },
            onConfirm = { newTitle ->
                onEdit(newTitle)
                showEditDialog = false
            }
        )
    }
}

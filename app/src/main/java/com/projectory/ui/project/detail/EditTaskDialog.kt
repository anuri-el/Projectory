package com.projectory.ui.project.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun EditTaskDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var taskTitle by remember { mutableStateOf(currentTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Task title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onConfirm(taskTitle)
                    }
                },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
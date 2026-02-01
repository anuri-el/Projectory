package com.projectory.ui.project.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tag") },
        text = {
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
                label = { Text("Tag name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (tagText.isNotBlank()) {
                        onConfirm(tagText.trim())
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
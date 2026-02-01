package com.projectory.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun QuickAddNoteDialog(
    projectId: Long,
    onDismiss: () -> Unit,
    onNoteAdded: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var noteContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Quick Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Your note") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 5,
                    placeholder = { Text("What did you accomplish?") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (noteContent.isNotBlank()) {
                                viewModel.addQuickNote(projectId, noteContent)
                                onNoteAdded()
                            }
                        },
                        enabled = noteContent.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
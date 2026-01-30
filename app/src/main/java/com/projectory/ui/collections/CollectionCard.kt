package com.projectory.ui.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CollectionCard(
    collectionWithProjects: CollectionWithProjects,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onProjectClick: (Long) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val collection = collectionWithProjects.collection
    val projects = collectionWithProjects.projects

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = try {
                Color(android.graphics.Color.parseColor(collection.color)).copy(alpha = 0.1f)
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(collection.color))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            collection.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (collection.description.isNotEmpty()) {
                            Text(
                                collection.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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

            Spacer(modifier = Modifier.height(16.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CollectionStat(
                    icon = Icons.Default.Folder,
                    label = "Projects",
                    value = projects.size.toString()
                )
                CollectionStat(
                    icon = Icons.Default.CheckCircle,
                    label = "Completed",
                    value = collectionWithProjects.completedCount.toString()
                )
                CollectionStat(
                    icon = Icons.Default.Timer,
                    label = "Time",
                    value = formatTime(collectionWithProjects.totalTime)
                )
            }

            // Projects
            if (projects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Projects in this collection:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projects) { project ->
                        ProjectChip(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Collection?") },
            text = {
                Text("Are you sure you want to delete '${collection.name}'? Projects will not be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    return if (hours > 0) "${hours}h" else "${(seconds / 60)}m"
}
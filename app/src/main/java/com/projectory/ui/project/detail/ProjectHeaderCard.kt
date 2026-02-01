package com.projectory.ui.project.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.projectory.domain.model.*
import java.time.format.DateTimeFormatter

@Composable
fun ProjectHeaderCard(project: Project?) {
    if (project == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (project.imagePath != null) {
                    AsyncImage(
                        model = project.imagePath,
                        contentDescription = project.title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            project.category.icon,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        project.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        project.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    if (project.description.isNotEmpty()) {
                        Text(
                            project.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (project.status) {
                    ProjectStatus.IN_PROGRESS -> Color(0xFF10B981)
                    ProjectStatus.PLANNED -> Color(0xFF6366F1)
                    ProjectStatus.COMPLETED -> Color(0xFF8B5CF6)
                    ProjectStatus.PAUSED -> Color(0xFFF59E0B)
                    ProjectStatus.GAVE_UP -> Color(0xFFEF4444)
                }.copy(alpha = 0.9f)
            ) {
                Text(
                    project.status.displayName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.CalendarToday,
                    label = if (project.startDate != null) "Started" else "Created",
                    value = (project.startDate ?: project.createdDate)
                        .format(DateTimeFormatter.ofPattern("MMM dd"))
                )
                StatItem(
                    icon = Icons.Default.Timer,
                    label = "Time Spent",
                    value = formatTime(project.totalTimeSpent)
                )
                if (project.tags.isNotEmpty()) {
                    StatItem(
                        icon = Icons.Default.Label,
                        label = "Tags",
                        value = project.tags.size.toString()
                    )
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}
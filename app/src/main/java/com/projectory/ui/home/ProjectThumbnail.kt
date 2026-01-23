package com.projectory.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.projectory.domain.model.Project

@Composable
fun ProjectThumbnail(
    project: Project,
    onClick: () -> Unit
) {
    if (project.imagePath != null) {
        AsyncImage(
            model = project.imagePath,
            contentDescription = project.title,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                project.category.icon,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
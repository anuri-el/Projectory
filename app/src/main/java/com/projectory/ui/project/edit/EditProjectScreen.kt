package com.projectory.ui.project.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.projectory.domain.model.ProjectStatus
import com.projectory.ui.project.add.CategorySelector
import com.projectory.ui.project.add.TagsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectScreen(
    onNavigateBack: () -> Unit,
    onProjectUpdated: () -> Unit,
    viewModel: EditProjectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTagDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateImageUri(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Project") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(
                            onClick = {
                                viewModel.saveProject {
                                    onProjectUpdated()
                                }
                            },
                            enabled = uiState.title.isNotBlank()
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Error message
            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }

            // Image Picker
            Text(
                "Project Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val imageSource = uiState.newImageUri ?: uiState.currentImagePath

                if (imageSource != null) {
                    AsyncImage(
                        model = imageSource,
                        contentDescription = "Project image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Change image button overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("Change Image")
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Add Project Image",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Project Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.title.isBlank() && uiState.errorMessage != null
            )

            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Category
            Text(
                "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CategorySelector(
                selectedCategory = uiState.category,
                onCategorySelected = { viewModel.updateCategory(it) }
            )

            // Status
            Text(
                "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProjectStatus.entries.forEach { status ->
                    FilterChip(
                        selected = uiState.status == status,
                        onClick = { viewModel.updateStatus(status) },
                        label = { Text(status.displayName) }
                    )
                }
            }

            // Tags
            Text(
                "Tags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TagsSection(
                tags = uiState.tags,
                onAddTag = { showTagDialog = true },
                onRemoveTag = { viewModel.removeTag(it) }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showTagDialog) {
        AddTagDialog(
            onDismiss = { showTagDialog = false },
            onConfirm = { tag ->
                viewModel.addTag(tag)
                showTagDialog = false
            }
        )
    }
}
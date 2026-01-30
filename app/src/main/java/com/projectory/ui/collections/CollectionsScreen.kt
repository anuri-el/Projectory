package com.projectory.ui.collections

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
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProject: (Long) -> Unit,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collections") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Collection")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.collections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        "No Collections Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Create collections to organize your projects",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Button(onClick = { viewModel.showAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create Collection")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.collections) { collectionWithProjects ->
                    CollectionCard(
                        collectionWithProjects = collectionWithProjects,
                        onEdit = { viewModel.showEditDialog(collectionWithProjects.collection) },
                        onDelete = { viewModel.deleteCollection(collectionWithProjects.collection) },
                        onProjectClick = onNavigateToProject
                    )
                }
            }
        }
    }

    // Add Dialog
    if (uiState.showAddDialog) {
        AddCollectionDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { name, description, color ->
                viewModel.addCollection(name, description, color)
            }
        )
    }

    // Edit Dialog
    if (uiState.showEditDialog && uiState.editingCollection != null) {
        EditCollectionDialog(
            collection = uiState.editingCollection!!,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { collection ->
                viewModel.updateCollection(collection)
            }
        )
    }
}
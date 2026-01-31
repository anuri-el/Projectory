package com.projectory.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun LibraryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProject: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Library") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showFilterSheet() }) {
                        val filterCount =
                            (if (uiState.filters.selectedStatus != null) 1 else 0) +
                                    uiState.filters.selectedCategories.size

                        BadgedBox(
                            badge = {
                                if (filterCount > 0) {
                                    Badge {
                                        Text(filterCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filters"
                            )
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
        ) {
            // Search Bar
            SearchBar(
                query = uiState.filters.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Results Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${uiState.filteredProjects.size} projects",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                // Sort indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        uiState.filters.sortOption.displayName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredProjects.isEmpty()) {
                EmptyLibraryState(
                    hasFilters = uiState.filters.searchQuery.isNotBlank() ||
                            uiState.filters.selectedStatus != null ||
                            uiState.filters.selectedCategories.isNotEmpty(),
                    onClearFilters = { viewModel.clearFilters() }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredProjects) { project ->
                        ProjectGridItem(
                            project = project,
                            onClick = { onNavigateToProject(project.id) }
                        )
                    }
                }
            }
        }
    }

    // Filter Sheet
    if (uiState.showFilterSheet) {
        FilterBottomSheet(
            filters = uiState.filters,
            onDismiss = { viewModel.hideFilterSheet() },
            onStatusChange = { viewModel.updateStatus(it) },
            onCategoryToggle = { viewModel.toggleCategory(it) },
            onSortChange = { viewModel.updateSortOption(it) },
            onClearAll = {
                viewModel.clearFilters()
                viewModel.hideFilterSheet()
            }
        )
    }
}
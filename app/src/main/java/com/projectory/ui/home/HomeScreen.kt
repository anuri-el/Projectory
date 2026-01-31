package com.projectory.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProject: (Long) -> Unit,
    onNavigateToAddProject: () -> Unit,
    onNavigateToInProgress: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDailyStats: () -> Unit,
    onNavigateToAnnualStats: () -> Unit,
    onNavigateToCollections: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Welcome :)",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "What project did you work on today?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }
            )
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Horizontal scrolling section: Add Project + In Progress Projects
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Add Project Card (First)
                    item {
                        AddProjectCard(
                            onClick = onNavigateToAddProject,
                            modifier = Modifier.width(280.dp)
                        )
                    }

                    // In Progress Projects
                    items(uiState.inProgressProjects) { project ->
                        InProgressProjectCard(
                            project = project,
                            onClick = { onNavigateToProject(project.id) },
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }

                // Current Projects Count Card (clickable to navigate)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToInProgress),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "You are working on ${uiState.inProgressProjects.size} projects.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "View all",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                // Quick Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Book Calendar",
                        subtitle = "How much have you read this month?",
                        icon = Icons.Default.CalendarMonth,
                        onClick = onNavigateToCalendar
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Streak",
                        subtitle = "${uiState.currentStreak} days",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFFF6B35),
                        onClick = onNavigateToCalendar
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Daily statistics",
                        subtitle = "${uiState.dailyProgress}%",
                        icon = Icons.Default.TrendingUp,
                        onClick = onNavigateToDailyStats
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Annual statistics",
                        subtitle = "${uiState.annualProjectsCompleted} projects read",
                        icon = Icons.Default.BarChart,
                        onClick = onNavigateToAnnualStats
                    )
                }

                // Goals/Collections
                StatCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Goals",
                    subtitle = "View your collections",
                    icon = Icons.Default.Flag,
                    onClick = onNavigateToCollections
                )

                // My Library
                StatCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "My Library",
                    subtitle = "All projects",
                    icon = Icons.Default.LibraryBooks,
                    onClick = onNavigateToLibrary
                )

                // Projects to work on later (Planned projects)
                if (uiState.plannedProjects.isNotEmpty()) {
                    Text(
                        "Projects to work on later",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.plannedProjects) { project ->
                            PlannedProjectThumbnail(
                                project = project,
                                onClick = { onNavigateToProject(project.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.projectory.ui.home

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
                // Current Project Card
                if (uiState.recentProject != null) {
                    CurrentProjectCard(
                        project = uiState.recentProject!!,
                        onClick = { onNavigateToProject(uiState.recentProject!!.id) }
                    )
                } else {
                    AddProjectPrompt(onNavigateToAddProject)
                }

                // Active Projects Count
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            "You are working on ${uiState.activeProjects.size} projects.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Project icons placeholder
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
                        subtitle = "${uiState.annualBooksRead} projects read",
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

                // Projects to work on later
                if (uiState.activeProjects.size > 1) {
                    Text(
                        "Projects to work on later",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AddProjectButton(onClick = onNavigateToAddProject)
                        }
                        items(uiState.activeProjects.drop(1)) { project ->
                            ProjectThumbnail(
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
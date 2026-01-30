package com.projectory.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProject: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.currentMonth.format(
                            DateTimeFormatter.ofPattern("MMMM yyyy")
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToToday() }) {
                        Icon(Icons.Default.Today, contentDescription = "Today")
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
            ) {
                // Month Navigation
                MonthNavigationBar(
                    onPreviousMonth = { viewModel.goToPreviousMonth() },
                    onNextMonth = { viewModel.goToNextMonth() }
                )

                // Monthly Stats
                MonthlyStatsCard(stats = uiState.monthlyStats)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Calendar Grid
                CalendarGrid(
                    days = uiState.days,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.selectDate(it) }
                )
            }
        }
    }

    // Activity Detail Dialog
    if (uiState.selectedDate != null && uiState.activitiesForSelectedDate.isNotEmpty()) {
        ActivityDetailDialog(
            date = uiState.selectedDate!!,
            activities = uiState.activitiesForSelectedDate,
            projectsMap = uiState.projectsMap,
            onDismiss = { viewModel.clearSelectedDate() },
            onNavigateToProject = { projectId ->
                viewModel.clearSelectedDate()
                onNavigateToProject(projectId)
            }
        )
    }
}
package com.projectory.ui.statistics.annual

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun AnnualStatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnnualStatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annual Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToCurrentYear() }) {
                        Icon(Icons.Default.Today, contentDescription = "Current Year")
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
            ) {
                // Year Navigation
                YearNavigationBar(
                    year = uiState.selectedYear,
                    onPreviousYear = { viewModel.goToPreviousYear() },
                    onNextYear = { viewModel.goToNextYear() }
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overview Cards
                    YearOverviewCards(
                        totalProjects = uiState.totalProjects,
                        completedProjects = uiState.completedProjects,
                        activeProjects = uiState.activeProjects,
                        totalTime = uiState.totalTime
                    )

                    // Streak Card
                    StreakCard(
                        currentStreak = uiState.currentStreak,
                        longestStreak = uiState.longestStreak
                    )

                    // Monthly Activity Chart
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Monthly Activity",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MonthlyBarChart(
                                monthlyData = uiState.monthlyData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                    }

                    // Monthly Stats Grid
                    MonthlyStatsGrid(monthlyData = uiState.monthlyData)

                    // Category Breakdown
                    if (uiState.categoryStats.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Projects by Category",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                uiState.categoryStats.forEach { category ->
                                    CategoryStatItem(category)
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    // Year Summary
                    YearSummaryCard(
                        totalTime = uiState.totalTime,
                        totalTasks = uiState.totalTasks,
                        totalNotes = uiState.totalNotes
                    )
                }
            }
        }
    }
}
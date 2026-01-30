package com.projectory.ui.statistics.annual

import java.time.LocalDateTime

data class AnnualStatsUiState(
    val selectedYear: Int = LocalDateTime.now().year,
    val monthlyData: List<MonthlyData> = emptyList(),
    val categoryStats: List<CategoryStats> = emptyList(),
    val totalProjects: Int = 0,
    val completedProjects: Int = 0,
    val activeProjects: Int = 0,
    val totalTime: Long = 0,
    val totalTasks: Int = 0,
    val totalNotes: Int = 0,
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    val isLoading: Boolean = true
)
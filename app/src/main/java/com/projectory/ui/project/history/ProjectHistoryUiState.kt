package com.projectory.ui.project.history

import com.projectory.domain.model.Project

data class ProjectHistoryUiState(
    val project: Project? = null,
    val dailyActivities: List<DailyActivitySummary> = emptyList(),
    val totalTime: Long = 0,
    val totalTasks: Int = 0,
    val activeDays: Int = 0,
    val isLoading: Boolean = true
)
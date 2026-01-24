package com.projectory.ui.achievements

import com.projectory.domain.model.Project

data class AchievementsUiState(
    val completedProjects: List<Project> = emptyList(),
    val totalCompleted: Int = 0,
    val isLoading: Boolean = true
)
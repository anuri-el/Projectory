package com.projectory.ui.home

import com.projectory.domain.model.Project

data class HomeUiState(
    val inProgressProjects: List<Project> = emptyList(),
    val plannedProjects: List<Project> = emptyList(),
    val currentStreak: Int = 0,
    val dailyProgress: Int = 0,
    val annualProjectsCompleted: Int = 0,
    val isLoading: Boolean = true
)
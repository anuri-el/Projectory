package com.projectory.ui.home

import com.projectory.domain.model.Project

data class HomeUiState(
    val activeProjects: List<Project> = emptyList(),
    val recentProject: Project? = null,
    val currentStreak: Int = 0,
    val dailyProgress: Int = 0,
    val annualBooksRead: Int = 0,
    val isLoading: Boolean = true
)
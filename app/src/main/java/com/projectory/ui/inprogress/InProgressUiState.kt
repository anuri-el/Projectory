package com.projectory.ui.inprogress

import com.projectory.domain.model.Project

data class InProgressUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true
)
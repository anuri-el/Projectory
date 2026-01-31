package com.projectory.ui.library

import com.projectory.domain.model.Project

data class LibraryUiState(
    val allProjects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val filters: LibraryFilters = LibraryFilters(),
    val showFilterSheet: Boolean = false,
    val isLoading: Boolean = true
)
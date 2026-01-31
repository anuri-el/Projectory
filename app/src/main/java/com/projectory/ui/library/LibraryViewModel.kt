package com.projectory.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.Category
import com.projectory.domain.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getAllProjects()
                .collectLatest { projects ->
                    _uiState.update {
                        it.copy(
                            allProjects = projects,
                            isLoading = false
                        )
                    }
                    applyFilters()
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(filters = it.filters.copy(searchQuery = query))
        }
        applyFilters()
    }

    fun updateStatus(status: ProjectStatus?) {
        _uiState.update {
            it.copy(filters = it.filters.copy(selectedStatus = status))
        }
        applyFilters()
    }

    fun toggleCategory(category: Category) {
        _uiState.update { state ->
            val categories = state.filters.selectedCategories.toMutableSet()
            if (categories.contains(category)) {
                categories.remove(category)
            } else {
                categories.add(category)
            }
            state.copy(filters = state.filters.copy(selectedCategories = categories))
        }
        applyFilters()
    }

    fun updateSortOption(option: SortOption) {
        _uiState.update {
            it.copy(filters = it.filters.copy(sortOption = option))
        }
        applyFilters()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(filters = LibraryFilters())
        }
        applyFilters()
    }

    fun showFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = true) }
    }

    fun hideFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.allProjects

        // Search query
        if (state.filters.searchQuery.isNotBlank()) {
            filtered = filtered.filter { project ->
                project.title.contains(state.filters.searchQuery, ignoreCase = true) ||
                        project.description.contains(state.filters.searchQuery, ignoreCase = true) ||
                        project.tags.any { it.contains(state.filters.searchQuery, ignoreCase = true) }
            }
        }

        // Status filter
        state.filters.selectedStatus?.let { status ->
            filtered = filtered.filter { it.status == status }
        }

        // Category filter
        if (state.filters.selectedCategories.isNotEmpty()) {
            filtered = filtered.filter {
                state.filters.selectedCategories.contains(it.category)
            }
        }

        // Sort
        filtered = when (state.filters.sortOption) {
            SortOption.RECENT -> filtered.sortedByDescending { it.createdDate }
            SortOption.OLDEST -> filtered.sortedBy { it.createdDate }
            SortOption.NAME_AZ -> filtered.sortedBy { it.title.lowercase() }
            SortOption.NAME_ZA -> filtered.sortedByDescending { it.title.lowercase() }
            SortOption.PROGRESS_HIGH -> filtered.sortedByDescending { it.progress }
            SortOption.PROGRESS_LOW -> filtered.sortedBy { it.progress }
            SortOption.TIME_HIGH -> filtered.sortedByDescending { it.totalTimeSpent }
            SortOption.TIME_LOW -> filtered.sortedBy { it.totalTimeSpent }
        }

        _uiState.update { it.copy(filteredProjects = filtered) }
    }
}
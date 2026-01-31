package com.projectory.ui.inprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InProgressViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InProgressUiState())
    val uiState: StateFlow<InProgressUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getProjectsByStatus(ProjectStatus.IN_PROGRESS)
                .collectLatest { projects ->
                    _uiState.update {
                        it.copy(
                            projects = projects,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
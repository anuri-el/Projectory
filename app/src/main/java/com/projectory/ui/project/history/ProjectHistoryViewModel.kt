package com.projectory.ui.project.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId") ?: 0L

    private val _uiState = MutableStateFlow(ProjectHistoryUiState())
    val uiState: StateFlow<ProjectHistoryUiState> = _uiState.asStateFlow()

    init {
        loadProjectHistory()
    }

    private fun loadProjectHistory() {
        viewModelScope.launch {
            combine(
                projectRepository.getProjectById(projectId),
                activityRepository.getActivitiesForProject(projectId)
            ) { project, activities ->
                Pair(project, activities)
            }.collectLatest { (project, activities) ->
                // Group activities by date
                val dailyActivities = activities
                    .groupBy { it.date.toLocalDate() }
                    .map { (date, dayActivities) ->
                        DailyActivitySummary(
                            date = date,
                            timeSpent = dayActivities.sumOf { it.timeSpent },
                            tasksCompleted = dayActivities.sumOf { it.tasksCompleted },
                            notesAdded = dayActivities.sumOf { it.notesAdded }
                        )
                    }
                    .sortedByDescending { it.date }

                val totalTime = activities.sumOf { it.timeSpent }
                val totalTasks = activities.sumOf { it.tasksCompleted }
                val activeDays = dailyActivities.size

                _uiState.update {
                    it.copy(
                        project = project,
                        dailyActivities = dailyActivities,
                        totalTime = totalTime,
                        totalTasks = totalTasks,
                        activeDays = activeDays,
                        isLoading = false
                    )
                }
            }
        }
    }
}
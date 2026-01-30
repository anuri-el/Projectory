package com.projectory.ui.statistics.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyStatsViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyStatsUiState())
    val uiState: StateFlow<DailyStatsUiState> = _uiState.asStateFlow()

    init {
        loadDailyStats()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, isLoading = true) }
        loadDailyStats()
    }

    private fun loadDailyStats() {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate
            val startOfDay = date.atStartOfDay()
            val endOfDay = date.atTime(23, 59, 59)

            combine(
                activityRepository.getActivitiesInRange(startOfDay, endOfDay),
                projectRepository.getAllProjects()
            ) { activities, projects ->
                Pair(activities, projects.associateBy { it.id })
            }.collectLatest { (activities, projectsMap) ->

                // Calculate hourly breakdown
                val hourlyMap = mutableMapOf<Int, HourlyActivity>()
                activities.forEach { activity ->
                    val hour = activity.date.hour
                    val current = hourlyMap[hour] ?: HourlyActivity(hour, 0, 0)
                    hourlyMap[hour] = current.copy(
                        timeSpent = current.timeSpent + activity.timeSpent,
                        tasksCompleted = current.tasksCompleted + activity.tasksCompleted
                    )
                }
                val hourlyActivities = (0..23).map { hour ->
                    hourlyMap[hour] ?: HourlyActivity(hour, 0, 0)
                }

                // Calculate project breakdown
                val projectTimeMap = mutableMapOf<Long, Long>()
                activities.forEach { activity ->
                    projectTimeMap[activity.projectId] =
                        (projectTimeMap[activity.projectId] ?: 0) + activity.timeSpent
                }

                val totalTime = projectTimeMap.values.sum()
                val projectBreakdown = projectTimeMap.mapNotNull { (projectId, time) ->
                    projectsMap[projectId]?.let { project ->
                        ProjectTimeBreakdown(
                            project = project,
                            timeSpent = time,
                            percentage = if (totalTime > 0) (time.toFloat() / totalTime) * 100 else 0f
                        )
                    }
                }.sortedByDescending { it.timeSpent }

                _uiState.update {
                    it.copy(
                        activities = activities,
                        hourlyActivities = hourlyActivities,
                        projectBreakdown = projectBreakdown,
                        totalTime = totalTime,
                        totalTasks = activities.sumOf { it.tasksCompleted },
                        totalNotes = activities.sumOf { it.notesAdded },
                        projectsWorkedOn = projectTimeMap.size,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun goToPreviousDay() {
        selectDate(_uiState.value.selectedDate.minusDays(1))
    }

    fun goToNextDay() {
        selectDate(_uiState.value.selectedDate.plusDays(1))
    }

    fun goToToday() {
        selectDate(LocalDate.now())
    }
}
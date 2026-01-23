package com.projectory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.CollectionRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val activityRepository: ActivityRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            projectRepository.getProjectsByStatus(ProjectStatus.ACTIVE)
                .combine(projectRepository.getProjectsByStatus(ProjectStatus.COMPLETED)) { active, completed ->
                    Pair(active, completed)
                }
                .collectLatest { (active, completed) ->
                    val streak = calculateStreak()
                    val today = LocalDateTime.now()
                    val startOfDay = today.withHour(0).withMinute(0).withSecond(0)
                    val endOfDay = today.withHour(23).withMinute(59).withSecond(59)

                    val dailyTime = activityRepository.getTotalTimeInRange(startOfDay, endOfDay)
                    val dailyProgress = ((dailyTime / 3600.0) * 100).toInt().coerceIn(0, 100)

                    val yearStart = LocalDateTime.of(today.year, 1, 1, 0, 0)
                    val completedThisYear = completed.count {
                        it.completedDate?.year == today.year
                    }

                    _uiState.update {
                        it.copy(
                            activeProjects = active.take(5),
                            recentProject = active.firstOrNull(),
                            currentStreak = streak,
                            dailyProgress = dailyProgress,
                            annualBooksRead = completedThisYear,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private suspend fun calculateStreak(): Int {
        var streak = 0
        var currentDate = LocalDate.now()

        while (true) {
            val startOfDay = currentDate.atStartOfDay()
            val endOfDay = currentDate.atTime(23, 59, 59)

            val activities = activityRepository.getActivitiesInRange(startOfDay, endOfDay)
                .first()

            if (activities.isEmpty()) {
                break
            }

            streak++
            currentDate = currentDate.minusDays(1)
        }

        return streak
    }

    fun refreshData() {
        loadData()
    }
}
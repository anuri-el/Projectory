package com.projectory.ui.statistics.annual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AnnualStatsViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnualStatsUiState())
    val uiState: StateFlow<AnnualStatsUiState> = _uiState.asStateFlow()

    init {
        loadAnnualStats()
    }

    fun selectYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year, isLoading = true) }
        loadAnnualStats()
    }

    private fun loadAnnualStats() {
        viewModelScope.launch {
            val year = _uiState.value.selectedYear
            val startOfYear = LocalDateTime.of(year, 1, 1, 0, 0)
            val endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59)

            combine(
                activityRepository.getActivitiesInRange(startOfYear, endOfYear),
                projectRepository.getAllProjects()
            ) { activities, projects ->
                Pair(activities, projects)
            }.collectLatest { (activities, allProjects) ->

                // Monthly breakdown
                val monthlyMap = mutableMapOf<Int, MutableList<Long>>()
                val monthlyTasks = mutableMapOf<Int, Int>()
                val monthlyNotes = mutableMapOf<Int, Int>()
                val monthlyActiveDays = mutableMapOf<Int, MutableSet<Int>>()

                activities.forEach { activity ->
                    val month = activity.date.monthValue
                    monthlyMap.getOrPut(month) { mutableListOf() }.add(activity.timeSpent)
                    monthlyTasks[month] = (monthlyTasks[month] ?: 0) + activity.tasksCompleted
                    monthlyNotes[month] = (monthlyNotes[month] ?: 0) + activity.notesAdded
                    monthlyActiveDays.getOrPut(month) { mutableSetOf() }.add(activity.date.dayOfMonth)
                }

                val monthNames = listOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val monthlyData = (1..12).map { month ->
                    MonthlyData(
                        month = month,
                        monthName = monthNames[month - 1],
                        timeSpent = monthlyMap[month]?.sum() ?: 0,
                        tasksCompleted = monthlyTasks[month] ?: 0,
                        notesAdded = monthlyNotes[month] ?: 0,
                        activeDays = monthlyActiveDays[month]?.size ?: 0
                    )
                }

                // Category stats
                val projectsByCategory = allProjects.groupBy { it.category }
                val categoryStats = projectsByCategory.map { (category, projects) ->
                    val categoryTime = activities
                        .filter { activity -> projects.any { it.id == activity.projectId } }
                        .sumOf { it.timeSpent }

                    val completed = projects.count { it.status == ProjectStatus.COMPLETED }
                    val completionRate = if (projects.isNotEmpty()) {
                        (completed.toFloat() / projects.size) * 100
                    } else 0f

                    CategoryStats(
                        categoryName = category.displayName,
                        categoryIcon = category.icon,
                        projectCount = projects.size,
                        timeSpent = categoryTime,
                        completionRate = completionRate
                    )
                }.sortedByDescending { it.timeSpent }

                // Calculate streaks
                val (current, longest) = calculateStreaks(activities)

                // Project stats
                val yearProjects = allProjects.filter {
                    it.createdDate.year == year
                }
                val completed = yearProjects.count { it.status == ProjectStatus.COMPLETED }
                val active = yearProjects.count { it.status == ProjectStatus.ACTIVE }

                _uiState.update {
                    it.copy(
                        monthlyData = monthlyData,
                        categoryStats = categoryStats,
                        totalProjects = yearProjects.size,
                        completedProjects = completed,
                        activeProjects = active,
                        totalTime = activities.sumOf { it.timeSpent },
                        totalTasks = activities.sumOf { it.tasksCompleted },
                        totalNotes = activities.sumOf { it.notesAdded },
                        currentStreak = current,
                        longestStreak = longest,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun calculateStreaks(activities: List<com.projectory.domain.model.Activity>): Pair<Int, Int> {
        val activeDates = activities.map { it.date.toLocalDate() }.toSet().sorted()
        if (activeDates.isEmpty()) return Pair(0, 0)

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 1

        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)

        // Check current streak
        if (activeDates.contains(today) || activeDates.contains(yesterday)) {
            var checkDate = if (activeDates.contains(today)) today else yesterday
            currentStreak = 1

            while (activeDates.contains(checkDate.minusDays(1))) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            }
        }

        // Calculate longest streak
        for (i in 1 until activeDates.size) {
            if (activeDates[i].minusDays(1) == activeDates[i - 1]) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        longestStreak = maxOf(longestStreak, tempStreak)

        return Pair(currentStreak, longestStreak)
    }

    fun goToPreviousYear() {
        selectYear(_uiState.value.selectedYear - 1)
    }

    fun goToNextYear() {
        selectYear(_uiState.value.selectedYear + 1)
    }

    fun goToCurrentYear() {
        selectYear(LocalDateTime.now().year)
    }
}
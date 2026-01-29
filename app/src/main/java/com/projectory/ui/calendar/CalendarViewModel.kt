package com.projectory.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarData()
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getAllProjects()
                .collectLatest { projects ->
                    val projectsMap = projects.associateBy { it.id }
                    _uiState.update { it.copy(projectsMap = projectsMap) }
                }
        }
    }

    private fun loadCalendarData() {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val startDate = currentMonth.atDay(1).atStartOfDay()
            val endDate = currentMonth.atEndOfMonth().atTime(23, 59, 59)

            // Get activities for the month
            activityRepository.getActivitiesInRange(startDate, endDate)
                .collectLatest { activities ->
                    val activeDates = activities.groupBy {
                        it.date.toLocalDate()
                    }

                    // Generate calendar days
                    val days = generateCalendarDays(currentMonth, activeDates)

                    // Calculate stats
                    val stats = calculateMonthlyStats(activities)

                    _uiState.update {
                        it.copy(
                            days = days,
                            monthlyStats = stats,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun generateCalendarDays(
        month: YearMonth,
        activeDates: Map<LocalDate, List<Activity>>
    ): List<CalendarDay> {
        val firstDayOfMonth = month.atDay(1)
        val lastDayOfMonth = month.atEndOfMonth()
        val today = LocalDate.now()

        // Get first day of week for the month (Monday = 1, Sunday = 7)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // Days from previous month
        val daysFromPrevMonth = (firstDayOfWeek - 1).let {
            if (it < 0) 6 else it
        }
        val prevMonth = month.minusMonths(1)
        val prevMonthDays = mutableListOf<CalendarDay>()
        for (i in daysFromPrevMonth downTo 1) {
            val date = prevMonth.atEndOfMonth().minusDays(i.toLong() - 1)
            prevMonthDays.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = false,
                    isToday = date == today,
                    hasActivity = activeDates.containsKey(date),
                    activityCount = activeDates[date]?.size ?: 0
                )
            )
        }

        // Days of current month
        val currentMonthDays = (1..lastDayOfMonth.dayOfMonth).map { day ->
            val date = month.atDay(day)
            CalendarDay(
                date = date,
                isCurrentMonth = true,
                isToday = date == today,
                hasActivity = activeDates.containsKey(date),
                activityCount = activeDates[date]?.size ?: 0
            )
        }

        // Days from next month to complete the grid
        val totalDays = prevMonthDays.size + currentMonthDays.size
        val daysToAdd = (7 - (totalDays % 7)) % 7
        val nextMonth = month.plusMonths(1)
        val nextMonthDays = (1..daysToAdd).map { day ->
            val date = nextMonth.atDay(day)
            CalendarDay(
                date = date,
                isCurrentMonth = false,
                isToday = date == today,
                hasActivity = activeDates.containsKey(date),
                activityCount = activeDates[date]?.size ?: 0
            )
        }

        return prevMonthDays + currentMonthDays + nextMonthDays
    }

    private fun calculateMonthlyStats(activities: List<Activity>): MonthlyStats {
        val uniqueDates = activities.map { it.date.toLocalDate() }.toSet()
        val totalTime = activities.sumOf { it.timeSpent }
        val uniqueProjects = activities.map { it.projectId }.toSet()

        return MonthlyStats(
            totalDays = _uiState.value.currentMonth.lengthOfMonth(),
            activeDays = uniqueDates.size,
            totalTimeSpent = totalTime,
            projectsWorkedOn = uniqueProjects.size
        )
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val startOfDay = date.atStartOfDay()
            val endOfDay = date.atTime(23, 59, 59)

            activityRepository.getActivitiesInRange(startOfDay, endOfDay)
                .first()
                .let { activities ->
                    _uiState.update {
                        it.copy(
                            selectedDate = date,
                            activitiesForSelectedDate = activities
                        )
                    }
                }
        }
    }

    fun clearSelectedDate() {
        _uiState.update { it.copy(selectedDate = null, activitiesForSelectedDate = emptyList()) }
    }

    fun goToPreviousMonth() {
        _uiState.update {
            it.copy(
                currentMonth = it.currentMonth.minusMonths(1),
                isLoading = true
            )
        }
        loadCalendarData()
    }

    fun goToNextMonth() {
        _uiState.update {
            it.copy(
                currentMonth = it.currentMonth.plusMonths(1),
                isLoading = true
            )
        }
        loadCalendarData()
    }

    fun goToToday() {
        val today = YearMonth.now()
        if (_uiState.value.currentMonth != today) {
            _uiState.update {
                it.copy(
                    currentMonth = today,
                    isLoading = true
                )
            }
            loadCalendarData()
        }
        selectDate(LocalDate.now())
    }
}
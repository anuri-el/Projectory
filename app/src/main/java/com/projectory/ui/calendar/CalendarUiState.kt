package com.projectory.ui.calendar

import com.projectory.domain.model.Activity
import com.projectory.domain.model.Project
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate? = null,
    val activitiesForSelectedDate: List<Activity> = emptyList(),
    val projectsMap: Map<Long, Project> = emptyMap(),
    val isLoading: Boolean = true,
    val monthlyStats: MonthlyStats = MonthlyStats()
)
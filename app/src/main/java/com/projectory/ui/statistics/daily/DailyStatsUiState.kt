package com.projectory.ui.statistics.daily

import com.projectory.domain.model.Activity
import java.time.LocalDate

data class DailyStatsUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val activities: List<Activity> = emptyList(),
    val hourlyActivities: List<HourlyActivity> = emptyList(),
    val projectBreakdown: List<ProjectTimeBreakdown> = emptyList(),
    val totalTime: Long = 0,
    val totalTasks: Int = 0,
    val totalNotes: Int = 0,
    val projectsWorkedOn: Int = 0,
    val isLoading: Boolean = true
)
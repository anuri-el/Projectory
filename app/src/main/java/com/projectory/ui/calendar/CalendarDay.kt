package com.projectory.ui.calendar

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val hasActivity: Boolean,
    val activityCount: Int = 0
)
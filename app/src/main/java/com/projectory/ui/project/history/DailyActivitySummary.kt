package com.projectory.ui.project.history

import java.time.LocalDate

data class DailyActivitySummary(
    val date: LocalDate,
    val timeSpent: Long,
    val tasksCompleted: Int,
    val notesAdded: Int
)
package com.projectory.ui.statistics.annual

data class MonthlyData(
    val month: Int,
    val monthName: String,
    val timeSpent: Long,
    val tasksCompleted: Int,
    val notesAdded: Int,
    val activeDays: Int
)
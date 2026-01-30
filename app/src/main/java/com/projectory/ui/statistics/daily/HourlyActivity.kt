package com.projectory.ui.statistics.daily

data class HourlyActivity(
    val hour: Int,
    val timeSpent: Long,
    val tasksCompleted: Int
)
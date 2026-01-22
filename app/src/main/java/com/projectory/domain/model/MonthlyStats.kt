package com.projectory.domain.model

data class MonthlyStats(
    val month: Int,
    val year: Int,
    val totalTime: Long,
    val projectsCompleted: Int,
    val totalTasks: Int
)
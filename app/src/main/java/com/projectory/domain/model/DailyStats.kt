package com.projectory.domain.model

import java.time.LocalDateTime

data class DailyStats(
    val date: LocalDateTime,
    val totalTime: Long,
    val projectsWorkedOn: Int,
    val tasksCompleted: Int,
    val notesAdded: Int
)
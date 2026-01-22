package com.projectory.domain.model

import java.time.LocalDateTime

data class Activity(
    val id: Long = 0,
    val projectId: Long,
    val date: LocalDateTime,
    val timeSpent: Long = 0,
    val tasksCompleted: Int = 0,
    val notesAdded: Int = 0
)
package com.projectory.domain.model

import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val order: Int = 0,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val completedDate: LocalDateTime? = null
)
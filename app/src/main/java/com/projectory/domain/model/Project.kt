package com.projectory.domain.model

import java.time.LocalDateTime

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val imagePath: String? = null,
    val category: Category,
    val status: ProjectStatus,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val completedDate: LocalDateTime? = null,
    val totalTimeSpent: Long = 0,
    val progress: Float = 0f,
    val tags: List<String> = emptyList(),
    val collections: List<Collection> = emptyList()
)
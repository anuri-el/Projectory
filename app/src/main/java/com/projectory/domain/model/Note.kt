package com.projectory.domain.model

import java.time.LocalDateTime

data class Note(
    val id: Long = 0,
    val projectId: Long,
    val content: String,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val imagePaths: List<String> = emptyList()
)
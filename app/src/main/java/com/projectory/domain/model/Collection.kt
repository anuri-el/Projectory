package com.projectory.domain.model

import java.time.LocalDateTime

data class Collection(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String = "#6366F1",
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val projectCount: Int = 0
)
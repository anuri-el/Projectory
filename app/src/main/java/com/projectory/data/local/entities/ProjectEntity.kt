package com.projectory.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val imagePath: String? = null,
    val category: String, // Life area
    val status: String, // "active", "completed", "paused"
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val startDate: LocalDateTime? = null,
    val completedDate: LocalDateTime? = null,
    val totalTimeSpent: Long = 0, // in seconds
    val progress: Float = 0f, // 0-100
    val tags: String = "" // comma-separated
)
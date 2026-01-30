package com.projectory.ui.collections

import com.projectory.domain.model.Collection
import com.projectory.domain.model.Project

data class CollectionWithProjects(
    val collection: Collection,
    val projects: List<Project>,
    val completedCount: Int,
    val totalTime: Long
)
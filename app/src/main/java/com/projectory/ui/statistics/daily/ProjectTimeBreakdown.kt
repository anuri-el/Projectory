package com.projectory.ui.statistics.daily

import com.projectory.domain.model.Project

data class ProjectTimeBreakdown(
    val project: Project,
    val timeSpent: Long,
    val percentage: Float
)
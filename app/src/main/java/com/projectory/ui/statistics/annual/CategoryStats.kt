package com.projectory.ui.statistics.annual

data class CategoryStats(
    val categoryName: String,
    val categoryIcon: String,
    val projectCount: Int,
    val timeSpent: Long,
    val completionRate: Float
)
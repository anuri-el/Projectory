package com.projectory.ui.statistics.annual

fun formatHours(seconds: Long): String {
    val hours = seconds / 3600
    return if (hours > 0) "${hours}h" else "${(seconds / 60)}m"
}
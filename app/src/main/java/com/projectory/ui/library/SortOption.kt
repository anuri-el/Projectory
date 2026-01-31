package com.projectory.ui.library

enum class SortOption(val displayName: String) {
    RECENT("Most Recent"),
    OLDEST("Oldest"),
    NAME_AZ("Name (A-Z)"),
    NAME_ZA("Name (Z-A)"),
    PROGRESS_HIGH("Progress (High)"),
    PROGRESS_LOW("Progress (Low)"),
    TIME_HIGH("Time (Most)"),
    TIME_LOW("Time (Least)")
}
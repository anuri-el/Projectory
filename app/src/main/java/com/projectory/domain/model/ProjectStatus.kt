package com.projectory.domain.model

enum class ProjectStatus(val displayName: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    PAUSED("Paused");

    companion object {
        fun fromString(value: String): ProjectStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
        }
    }
}
package com.projectory.domain.model

enum class ProjectStatus(val displayName: String) {
    IN_PROGRESS("In Progress"),
    PLANNED("Planned"),
    COMPLETED("Completed"),
    PAUSED("Paused"),
    GAVE_UP("Gave Up");

    companion object {
        fun fromString(value: String): ProjectStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PLANNED
        }
    }
}
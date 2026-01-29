package com.projectory.ui.project.detail

import com.projectory.domain.model.Note
import com.projectory.domain.model.Project
import com.projectory.domain.model.Task

data class ProjectDetailUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val isTimerRunning: Boolean = false,
    val timerSeconds: Long = 0,
    val showAddTaskDialog: Boolean = false,
    val showAddNoteDialog: Boolean = false,
    val showEditProjectDialog: Boolean = false
)
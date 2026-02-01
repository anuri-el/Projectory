package com.projectory.ui.timer

import com.projectory.domain.model.Note
import com.projectory.domain.model.Project
import com.projectory.domain.model.Task

data class TimerUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val notes: List<Note> = emptyList(),
    val isTimerRunning: Boolean = false,
    val timerSeconds: Long = 0,
    val notesAddedThisSession: Int = 0,
    val showSessionCompleteDialog: Boolean = false,
    val showAddNoteDialog: Boolean = false,
    val showNotesDialog: Boolean = false
)
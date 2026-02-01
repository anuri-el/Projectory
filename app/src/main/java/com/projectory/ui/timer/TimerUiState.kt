package com.projectory.ui.timer

import com.projectory.domain.model.Note
import com.projectory.domain.model.Project

data class TimerUiState(
    val project: Project? = null,
    val notes: List<Note> = emptyList(),
    val isTimerRunning: Boolean = false,
    val timerSeconds: Long = 0,
    val notesAddedThisSession: Int = 0,
    val showAddNoteDialog: Boolean = false,
    val showNotesDialog: Boolean = false
)
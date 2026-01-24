package com.projectory.ui.memories

data class MemoriesUiState(
    val notes: List<NoteWithProject> = emptyList(),
    val isLoading: Boolean = true
)
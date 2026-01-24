package com.projectory.ui.memories

import com.projectory.domain.model.Note
import com.projectory.domain.model.Project

data class NoteWithProject(
    val note: Note,
    val project: Project?
)
package com.projectory.ui.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.NoteRepository
import com.projectory.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .collectLatest { notes ->
                    val notesWithProjects = notes.map { note ->
                        val project = projectRepository.getProjectById(note.projectId).first()
                        NoteWithProject(note, project)
                    }
                    _uiState.update {
                        it.copy(
                            notes = notesWithProjects,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
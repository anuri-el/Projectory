package com.projectory.ui.project.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.*
import com.projectory.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId") ?: 0L

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadProjectDetails()
    }

    private fun loadProjectDetails() {
        viewModelScope.launch {
            combine(
                projectRepository.getProjectById(projectId),
                taskRepository.getTasksForProject(projectId),
                noteRepository.getNotesForProject(projectId)
            ) { project, tasks, notes ->
                Triple(project, tasks, notes)
            }.collectLatest { (project, tasks, notes) ->
                _uiState.update {
                    it.copy(
                        project = project,
                        tasks = tasks,
                        notes = notes,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task.id)

            // Log activity
            if (!task.isCompleted) {
                val today = LocalDateTime.now()
                activityRepository.insertActivity(
                    Activity(
                        projectId = projectId,
                        date = today,
                        tasksCompleted = 1
                    )
                )
            }
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                projectId = projectId,
                title = title,
                order = _uiState.value.tasks.size
            )
            taskRepository.insertTask(task)
            _uiState.update { it.copy(showAddTaskDialog = false) }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun addNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val note = Note(
                projectId = projectId,
                content = content
            )
            noteRepository.insertNote(note)

            // Log activity
            val today = LocalDateTime.now()
            activityRepository.insertActivity(
                Activity(
                    projectId = projectId,
                    date = today,
                    notesAdded = 1
                )
            )

            _uiState.update { it.copy(showAddNoteDialog = false) }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun updateProjectStatus(status: ProjectStatus) {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                val updatedProject = project.copy(
                    status = status,
                    completedDate = if (status == ProjectStatus.COMPLETED)
                        LocalDateTime.now() else null
                )
                projectRepository.updateProject(updatedProject)
            }
        }
    }

    fun showAddTaskDialog() {
        _uiState.update { it.copy(showAddTaskDialog = true) }
    }

    fun hideAddTaskDialog() {
        _uiState.update { it.copy(showAddTaskDialog = false) }
    }

    fun showAddNoteDialog() {
        _uiState.update { it.copy(showAddNoteDialog = true) }
    }

    fun hideAddNoteDialog() {
        _uiState.update { it.copy(showAddNoteDialog = false) }
    }

    fun showEditProjectDialog() {
        _uiState.update { it.copy(showEditProjectDialog = true) }
    }

    fun hideEditProjectDialog() {
        _uiState.update { it.copy(showEditProjectDialog = false) }
    }
}
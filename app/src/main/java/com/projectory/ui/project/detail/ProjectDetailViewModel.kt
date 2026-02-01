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

    fun startProject() {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                if (project.status == ProjectStatus.PLANNED) {
                    val updatedProject = project.copy(
                        status = ProjectStatus.IN_PROGRESS,
                        startDate = LocalDateTime.now()
                    )
                    projectRepository.updateProject(updatedProject)
                }
            }
        }
    }

    fun deleteProject() {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                projectRepository.deleteProject(project)
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

    fun editTask(task: Task, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            val updatedTask = task.copy(title = newTitle)
            taskRepository.updateTask(updatedTask)
        }
    }

    fun moveTaskUp(index: Int) {
        viewModelScope.launch {
            val tasks = _uiState.value.tasks
            if (index <= 0 || index >= tasks.size) return@launch

            val currentTask = tasks[index]
            val previousTask = tasks[index - 1]

            // Skip if previous task is completed
            if (previousTask.isCompleted) return@launch

            // Swap orders
            taskRepository.updateTask(currentTask.copy(order = previousTask.order))
            taskRepository.updateTask(previousTask.copy(order = currentTask.order))
        }
    }

    fun moveTaskDown(index: Int) {
        viewModelScope.launch {
            val tasks = _uiState.value.tasks
            if (index < 0 || index >= tasks.size - 1) return@launch

            val currentTask = tasks[index]
            val nextTask = tasks[index + 1]

            // Skip if next task is completed
            if (nextTask.isCompleted) return@launch

            // Swap orders
            taskRepository.updateTask(currentTask.copy(order = nextTask.order))
            taskRepository.updateTask(nextTask.copy(order = currentTask.order))
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
}
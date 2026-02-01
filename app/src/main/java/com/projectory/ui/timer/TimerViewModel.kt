package com.projectory.ui.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.NoteRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.data.repository.TaskRepository
import com.projectory.domain.model.Activity
import com.projectory.domain.model.Note
import com.projectory.domain.model.ProjectStatus
import com.projectory.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId") ?: 0L

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerStartTime: Long = 0L
    private var accumulatedTime: Long = 0L

    init {
        loadProjectDetails()
        startTimer()
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
                        notes = notes
                    )
                }
            }
        }
    }

    fun startTimer() {
        timerStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isTimerRunning = true) }

        viewModelScope.launch {
            while (_uiState.value.isTimerRunning) {
                kotlinx.coroutines.delay(1000)
                val elapsed = (System.currentTimeMillis() - timerStartTime) / 1000
                _uiState.update { it.copy(timerSeconds = accumulatedTime + elapsed) }
            }
        }
    }

    fun pauseTimer() {
        if (_uiState.value.isTimerRunning) {
            val elapsed = (System.currentTimeMillis() - timerStartTime) / 1000
            accumulatedTime += elapsed
            _uiState.update {
                it.copy(
                    isTimerRunning = false,
                    timerSeconds = accumulatedTime
                )
            }
        }
    }

    fun resumeTimer() {
        timerStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isTimerRunning = true) }

        viewModelScope.launch {
            while (_uiState.value.isTimerRunning) {
                kotlinx.coroutines.delay(1000)
                val elapsed = (System.currentTimeMillis() - timerStartTime) / 1000
                _uiState.update { it.copy(timerSeconds = accumulatedTime + elapsed) }
            }
        }
    }

    fun stopTimer() {
        pauseTimer()
    }

    fun showSessionCompleteDialog() {
        _uiState.update { it.copy(showSessionCompleteDialog = true) }
    }

    fun hideSessionCompleteDialog() {
        _uiState.update { it.copy(showSessionCompleteDialog = false) }
    }

    fun updateProjectStatus(status: ProjectStatus) {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                val updatedProject = project.copy(
                    status = status,
                    completedDate = if (status == ProjectStatus.COMPLETED)
                        LocalDateTime.now()
                    else
                        null
                )
                projectRepository.updateProject(updatedProject)
            }
        }
    }

    fun markTasksAsCompleted(taskIds: List<Long>) {
        viewModelScope.launch {
            taskIds.forEach { taskId ->
                taskRepository.toggleTaskCompletion(taskId)
            }
        }
    }

    fun completeSession() {
        val totalTime = _uiState.value.timerSeconds

        if (totalTime > 0) {
            viewModelScope.launch {
                // Save time to project
                projectRepository.addTimeToProject(projectId, totalTime)

                // Log activity
                val today = LocalDateTime.now()
                activityRepository.insertActivity(
                    Activity(
                        projectId = projectId,
                        date = today,
                        timeSpent = totalTime,
                        notesAdded = _uiState.value.notesAddedThisSession
                    )
                )
            }
        }
    }

    fun cancelSession() {
        pauseTimer()
        // Don't save anything - session is cancelled
    }

    fun addNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val note = Note(
                projectId = projectId,
                content = content
            )
            noteRepository.insertNote(note)
            _uiState.update {
                it.copy(
                    showAddNoteDialog = false,
                    notesAddedThisSession = it.notesAddedThisSession + 1
                )
            }
        }
    }

    fun showAddNoteDialog() {
        _uiState.update { it.copy(showAddNoteDialog = true) }
    }

    fun hideAddNoteDialog() {
        _uiState.update { it.copy(showAddNoteDialog = false) }
    }

    fun showNotesDialog() {
        _uiState.update { it.copy(showNotesDialog = true) }
    }

    fun hideNotesDialog() {
        _uiState.update { it.copy(showNotesDialog = false) }
    }
}
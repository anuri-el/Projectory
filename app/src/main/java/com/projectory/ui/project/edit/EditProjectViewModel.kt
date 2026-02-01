package com.projectory.ui.project.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.Category
import com.projectory.domain.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProjectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId") ?: 0L

    private val _uiState = MutableStateFlow(EditProjectUiState())
    val uiState: StateFlow<EditProjectUiState> = _uiState.asStateFlow()

    init {
        loadProject()
    }

    private fun loadProject() {
        viewModelScope.launch {
            projectRepository.getProjectById(projectId)
                .firstOrNull()
                ?.let { project ->
                    _uiState.update {
                        it.copy(
                            title = project.title,
                            description = project.description,
                            category = project.category,
                            currentImagePath = project.imagePath,
                            tags = project.tags,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCategory(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.update { it.copy(newImageUri = uri) }
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_uiState.value.tags.contains(tag)) {
            _uiState.update {
                it.copy(tags = it.tags + tag)
            }
        }
    }

    fun removeTag(tag: String) {
        _uiState.update {
            it.copy(tags = it.tags - tag)
        }
    }

    fun saveProject(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val project = projectRepository.getProjectById(projectId).firstOrNull()
                if (project == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Project not found"
                        )
                    }
                    return@launch
                }

                // Save new image if selected
                val imagePath = if (state.newImageUri != null) {
                    saveImageToInternalStorage(state.newImageUri)
                } else {
                    state.currentImagePath
                }

                // Update project (keep existing status, dates)
                val updatedProject = project.copy(
                    title = state.title,
                    description = state.description,
                    category = state.category,
                    imagePath = imagePath,
                    tags = state.tags
                )

                projectRepository.updateProject(updatedProject)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update project: ${e.message}"
                    )
                }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "project_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, "images")
            if (!file.exists()) {
                file.mkdirs()
            }
            val imageFile = File(file, fileName)

            inputStream?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }

            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
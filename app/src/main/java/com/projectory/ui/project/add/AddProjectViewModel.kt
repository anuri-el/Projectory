package com.projectory.ui.project.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.CollectionRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddProjectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectRepository: ProjectRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProjectUiState())
    val uiState: StateFlow<AddProjectUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            collectionRepository.getAllCollections()
                .collectLatest { collections ->
                    _uiState.update { it.copy(availableCollections = collections) }
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

    fun updateStatus(status: ProjectStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
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

    fun toggleCollection(collectionId: Long) {
        _uiState.update { state ->
            val selected = state.selectedCollections.toMutableList()
            if (selected.contains(collectionId)) {
                selected.remove(collectionId)
            } else {
                selected.add(collectionId)
            }
            state.copy(selectedCollections = selected)
        }
    }

    fun saveProject(onSuccess: (Long) -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Save image if selected
                val savedImagePath = state.imageUri?.let { uri ->
                    saveImageToInternalStorage(uri)
                }

                // Create project
                val project = Project(
                    title = state.title,
                    description = state.description,
                    category = state.category,
                    status = state.status,
                    imagePath = savedImagePath,
                    tags = state.tags,
                    createdDate = LocalDateTime.now()
                )

                val projectId = projectRepository.insertProject(project)

                // Add to collections
                state.selectedCollections.forEach { collectionId ->
                    collectionRepository.addProjectToCollection(projectId, collectionId)
                }

                _uiState.update { it.copy(isLoading = false) }
                onSuccess(projectId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save project: ${e.message}"
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
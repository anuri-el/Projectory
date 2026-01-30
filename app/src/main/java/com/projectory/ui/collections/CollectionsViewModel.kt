package com.projectory.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectory.data.repository.CollectionRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.domain.model.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            collectionRepository.getAllCollections()
                .collectLatest { collections ->
                    val collectionsWithProjects = collections.map { collection ->
                        val projects = collectionRepository.getCollectionWithProjects(collection.id).first()
                        val completed = projects.count { it.status == com.projectory.domain.model.ProjectStatus.COMPLETED }
                        val totalTime = projects.sumOf { it.totalTimeSpent }

                        CollectionWithProjects(
                            collection = collection,
                            projects = projects,
                            completedCount = completed,
                            totalTime = totalTime
                        )
                    }

                    _uiState.update {
                        it.copy(
                            collections = collectionsWithProjects,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(collection: Collection) {
        _uiState.update {
            it.copy(
                showEditDialog = true,
                editingCollection = collection
            )
        }
    }

    fun hideEditDialog() {
        _uiState.update {
            it.copy(
                showEditDialog = false,
                editingCollection = null
            )
        }
    }

    fun addCollection(name: String, description: String, color: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val collection = Collection(
                name = name,
                description = description,
                color = color
            )
            collectionRepository.insertCollection(collection)
            hideAddDialog()
        }
    }

    fun updateCollection(collection: Collection) {
        viewModelScope.launch {
            collectionRepository.updateCollection(collection)
            hideEditDialog()
        }
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collection)
        }
    }
}
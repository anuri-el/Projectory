package com.projectory.ui.project.add

import android.net.Uri
import com.projectory.domain.model.Category
import com.projectory.domain.model.Collection
import com.projectory.domain.model.ProjectStatus

data class AddProjectUiState(
    val title: String = "",
    val description: String = "",
    val category: Category = Category.PERSONAL,
    val status: ProjectStatus = ProjectStatus.PLANNED,
    val imageUri: Uri? = null,
    val imagePath: String? = null,
    val tags: List<String> = emptyList(),
    val selectedCollections: List<Long> = emptyList(),
    val availableCollections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
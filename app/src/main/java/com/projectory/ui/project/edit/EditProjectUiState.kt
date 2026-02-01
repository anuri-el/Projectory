package com.projectory.ui.project.edit

import android.net.Uri
import com.projectory.domain.model.Category
import com.projectory.domain.model.ProjectStatus

data class EditProjectUiState(
    val title: String = "",
    val description: String = "",
    val category: Category = Category.PERSONAL,
    val status: ProjectStatus = ProjectStatus.PLANNED,
    val currentImagePath: String? = null,
    val newImageUri: Uri? = null,
    val tags: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
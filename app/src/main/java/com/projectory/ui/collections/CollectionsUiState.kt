package com.projectory.ui.collections

import com.projectory.domain.model.Collection

data class CollectionsUiState(
    val collections: List<CollectionWithProjects> = emptyList(),
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingCollection: Collection? = null,
    val isLoading: Boolean = true
)
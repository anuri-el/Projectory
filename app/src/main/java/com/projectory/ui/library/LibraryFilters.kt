package com.projectory.ui.library

import com.projectory.domain.model.Category
import com.projectory.domain.model.ProjectStatus

data class LibraryFilters(
    val searchQuery: String = "",
    val selectedStatus: ProjectStatus? = null,
    val selectedCategories: Set<Category> = emptySet(),
    val sortOption: SortOption = SortOption.RECENT
)
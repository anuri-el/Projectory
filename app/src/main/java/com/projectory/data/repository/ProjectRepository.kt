package com.projectory.data.repository

import com.projectory.data.local.dao.CollectionDao
import com.projectory.data.local.dao.ProjectDao
import com.projectory.data.local.entities.ProjectEntity
import com.projectory.domain.model.Category
import com.projectory.domain.model.Project
import com.projectory.domain.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val collectionDao: CollectionDao
) {
    fun getAllProjects(): Flow<List<Project>> =
        projectDao.getAllProjects().map { it.map { entity -> entity.toDomain() } }

    fun getProjectById(id: Long): Flow<Project?> =
        projectDao.getProjectById(id).map { it?.toDomain() }

    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> =
        projectDao.getProjectsByStatus(status.name).map { it.map { entity -> entity.toDomain() } }

    suspend fun insertProject(project: Project): Long =
        projectDao.insertProject(project.toEntity())

    suspend fun updateProject(project: Project) =
        projectDao.updateProject(project.toEntity())

    suspend fun deleteProject(project: Project) =
        projectDao.deleteProject(project.toEntity())

    suspend fun addTimeToProject(projectId: Long, seconds: Long) =
        projectDao.addTimeToProject(projectId, seconds)

    suspend fun updateProgress(projectId: Long, progress: Float) =
        projectDao.updateProgress(projectId, progress)
}

fun ProjectEntity.toDomain() = Project(
    id = id,
    title = title,
    description = description,
    imagePath = imagePath,
    category = Category.fromString(category),
    status = ProjectStatus.fromString(status),
    createdDate = createdDate,
    completedDate = completedDate,
    totalTimeSpent = totalTimeSpent,
    progress = progress,
    tags = if (tags.isNotEmpty()) tags.split(",") else emptyList()
)

private fun Project.toEntity() = ProjectEntity(
    id = id,
    title = title,
    description = description,
    imagePath = imagePath,
    category = category.name,
    status = status.name,
    createdDate = createdDate,
    completedDate = completedDate,
    totalTimeSpent = totalTimeSpent,
    progress = progress,
    tags = tags.joinToString(",")
)

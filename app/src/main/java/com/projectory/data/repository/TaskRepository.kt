package com.projectory.data.repository

import com.projectory.data.local.dao.ProjectDao
import com.projectory.data.local.dao.TaskDao
import com.projectory.data.local.entities.TaskEntity
import com.projectory.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao
) {
    fun getTasksForProject(projectId: Long): Flow<List<Task>> =
        taskDao.getTasksForProject(projectId).map { it.map { entity -> entity.toDomain() } }

    suspend fun insertTask(task: Task): Long {
        val id = taskDao.insertTask(task.toEntity())
        updateProjectProgress(task.projectId)
        return id
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
        updateProjectProgress(task.projectId)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
        updateProjectProgress(task.projectId)
    }

    suspend fun toggleTaskCompletion(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        val updatedTask = task.copy(
            isCompleted = !task.isCompleted,
            completedDate = if (!task.isCompleted) LocalDateTime.now() else null
        )
        taskDao.updateTask(updatedTask)
        updateProjectProgress(task.projectId)
    }

    private suspend fun updateProjectProgress(projectId: Long) {
        val total = taskDao.getTaskCount(projectId)
        val completed = taskDao.getCompletedTaskCount(projectId)
        val progress = if (total > 0) (completed.toFloat() / total) * 100 else 0f
        projectDao.updateProgress(projectId, progress)
    }
}

private fun TaskEntity.toDomain() = Task(
    id = id,
    projectId = projectId,
    title = title,
    isCompleted = isCompleted,
    order = order,
    createdDate = createdDate,
    completedDate = completedDate
)

private fun Task.toEntity() = TaskEntity(
    id = id,
    projectId = projectId,
    title = title,
    isCompleted = isCompleted,
    order = order,
    createdDate = createdDate,
    completedDate = completedDate
)
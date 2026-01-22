package com.projectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.projectory.data.local.entities.ProjectEntity
import com.projectory.data.local.entities.ProjectWithCollections
import com.projectory.data.local.entities.ProjectWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdDate DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE status = :status ORDER BY createdDate DESC")
    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithCollections(projectId: Long): Flow<ProjectWithCollections?>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithTasks(projectId: Long): Flow<ProjectWithTasks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("UPDATE projects SET totalTimeSpent = totalTimeSpent + :timeInSeconds WHERE id = :projectId")
    suspend fun addTimeToProject(projectId: Long, timeInSeconds: Long)

    @Query("UPDATE projects SET progress = :progress WHERE id = :projectId")
    suspend fun updateProgress(projectId: Long, progress: Float)
}
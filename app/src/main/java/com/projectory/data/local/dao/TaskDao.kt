package com.projectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.projectory.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY `order` ASC, createdDate ASC")
    fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId")
    suspend fun getTaskCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId AND isCompleted = 1")
    suspend fun getCompletedTaskCount(projectId: Long): Int
}
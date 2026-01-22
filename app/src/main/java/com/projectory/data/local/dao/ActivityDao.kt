package com.projectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.projectory.data.local.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE projectId = :projectId ORDER BY date DESC")
    fun getActivitiesForProject(projectId: Long): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getActivitiesInRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ActivityEntity>>

    @Query("SELECT DISTINCT date(date) as activityDate FROM activities WHERE date >= :startDate AND date <= :endDate")
    suspend fun getActiveDatesInRange(startDate: LocalDateTime, endDate: LocalDateTime): List<String>

    @Query("SELECT * FROM activities WHERE date(date) = date(:date)")
    fun getActivitiesForDate(date: LocalDateTime): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("SELECT SUM(timeSpent) FROM activities WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalTimeInRange(startDate: LocalDateTime, endDate: LocalDateTime): Long?

    @Query("SELECT SUM(tasksCompleted) FROM activities WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalTasksCompletedInRange(startDate: LocalDateTime, endDate: LocalDateTime): Int?
}
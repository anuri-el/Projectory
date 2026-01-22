package com.projectory.data.repository

import com.projectory.data.local.dao.ActivityDao
import com.projectory.data.local.entities.ActivityEntity
import com.projectory.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val activityDao: ActivityDao
) {
    fun getActivitiesForProject(projectId: Long): Flow<List<Activity>> =
        activityDao.getActivitiesForProject(projectId).map { it.map { entity -> entity.toDomain() } }

    fun getActivitiesInRange(start: LocalDateTime, end: LocalDateTime): Flow<List<Activity>> =
        activityDao.getActivitiesInRange(start, end).map { it.map { entity -> entity.toDomain() } }

    fun getActivitiesForDate(date: LocalDateTime): Flow<List<Activity>> =
        activityDao.getActivitiesForDate(date).map { it.map { entity -> entity.toDomain() } }

    suspend fun insertActivity(activity: Activity): Long =
        activityDao.insertActivity(activity.toEntity())

    suspend fun getActiveDatesInMonth(year: Int, month: Int): List<String> {
        val start = LocalDateTime.of(year, month, 1, 0, 0)
        val end = start.plusMonths(1).minusDays(1)
        return activityDao.getActiveDatesInRange(start, end)
    }

    suspend fun getTotalTimeInRange(start: LocalDateTime, end: LocalDateTime): Long =
        activityDao.getTotalTimeInRange(start, end) ?: 0

    suspend fun getTotalTasksInRange(start: LocalDateTime, end: LocalDateTime): Int =
        activityDao.getTotalTasksCompletedInRange(start, end) ?: 0
}

private fun ActivityEntity.toDomain() = Activity(
    id = id,
    projectId = projectId,
    date = date,
    timeSpent = timeSpent,
    tasksCompleted = tasksCompleted,
    notesAdded = notesAdded
)

private fun Activity.toEntity() = ActivityEntity(
    id = id,
    projectId = projectId,
    date = date,
    timeSpent = timeSpent,
    tasksCompleted = tasksCompleted,
    notesAdded = notesAdded
)
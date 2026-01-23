package com.projectory.di

import com.projectory.data.local.dao.ActivityDao
import com.projectory.data.local.dao.CollectionDao
import com.projectory.data.local.dao.NoteDao
import com.projectory.data.local.dao.ProjectDao
import com.projectory.data.local.dao.TaskDao
import com.projectory.data.repository.ActivityRepository
import com.projectory.data.repository.CollectionRepository
import com.projectory.data.repository.NoteRepository
import com.projectory.data.repository.ProjectRepository
import com.projectory.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectDao: ProjectDao,
        collectionDao: CollectionDao
    ): ProjectRepository = ProjectRepository(projectDao, collectionDao)

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        projectDao: ProjectDao
    ): TaskRepository = TaskRepository(taskDao, projectDao)

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao
    ): NoteRepository = NoteRepository(noteDao)

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionDao: CollectionDao
    ): CollectionRepository = CollectionRepository(collectionDao)

    @Provides
    @Singleton
    fun provideActivityRepository(
        activityDao: ActivityDao
    ): ActivityRepository = ActivityRepository(activityDao)
}
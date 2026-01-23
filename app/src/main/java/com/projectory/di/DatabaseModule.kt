package com.projectory.di

import android.content.Context
import androidx.room.Room
import com.projectory.data.local.dao.ActivityDao
import com.projectory.data.local.dao.CollectionDao
import com.projectory.data.local.dao.NoteDao
import com.projectory.data.local.dao.ProjectDao
import com.projectory.data.local.dao.TaskDao
import com.projectory.data.local.database.ProjectoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideProjectoryDatabase(
        @ApplicationContext context: Context
    ): ProjectoryDatabase {
        return Room.databaseBuilder(
            context,
            ProjectoryDatabase::class.java,
            "projectory_database"
        ).build()
    }

    @Provides
    fun provideProjectDao(database: ProjectoryDatabase): ProjectDao =
        database.projectDao()

    @Provides
    fun provideTaskDao(database: ProjectoryDatabase): TaskDao =
        database.taskDao()

    @Provides
    fun provideNoteDao(database: ProjectoryDatabase): NoteDao =
        database.noteDao()

    @Provides
    fun provideCollectionDao(database: ProjectoryDatabase): CollectionDao =
        database.collectionDao()

    @Provides
    fun provideActivityDao(database: ProjectoryDatabase): ActivityDao =
        database.activityDao()
}
package com.projectory.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projectory.data.local.dao.*
import com.projectory.data.local.entities.*

@Database(
    entities = [
        ProjectEntity::class,
        TaskEntity::class,
        NoteEntity::class,
        CollectionEntity::class,
        ProjectCollectionCrossRef::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ProjectoryDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun collectionDao(): CollectionDao
    abstract fun activityDao(): ActivityDao
}
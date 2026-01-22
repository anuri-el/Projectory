package com.projectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.projectory.data.local.entities.CollectionEntity
import com.projectory.data.local.entities.CollectionWithProjects
import com.projectory.data.local.entities.ProjectCollectionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY createdDate DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionById(collectionId: Long): Flow<CollectionEntity?>

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionWithProjects(collectionId: Long): Flow<CollectionWithProjects?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectCollectionCrossRef(crossRef: ProjectCollectionCrossRef)

    @Delete
    suspend fun deleteProjectCollectionCrossRef(crossRef: ProjectCollectionCrossRef)

    @Query("DELETE FROM project_collection_cross_ref WHERE projectId = :projectId AND collectionId = :collectionId")
    suspend fun removeProjectFromCollection(projectId: Long, collectionId: Long)
}
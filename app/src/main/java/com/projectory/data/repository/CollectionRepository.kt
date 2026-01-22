package com.projectory.data.repository

import com.projectory.data.local.dao.CollectionDao
import com.projectory.data.local.entities.CollectionEntity
import com.projectory.data.local.entities.ProjectCollectionCrossRef
import com.projectory.domain.model.Collection
import com.projectory.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepository @Inject constructor(
    private val collectionDao: CollectionDao
) {
    fun getAllCollections(): Flow<List<Collection>> =
        collectionDao.getAllCollections().map { it.map { entity -> entity.toDomain() } }

    fun getCollectionById(id: Long): Flow<Collection?> =
        collectionDao.getCollectionById(id).map { it?.toDomain() }

    fun getCollectionWithProjects(id: Long): Flow<List<Project>> =
        collectionDao.getCollectionWithProjects(id).map {
            it?.projects?.map { p -> p.toDomain() } ?: emptyList()
        }

    suspend fun insertCollection(collection: Collection): Long =
        collectionDao.insertCollection(collection.toEntity())

    suspend fun updateCollection(collection: Collection) =
        collectionDao.updateCollection(collection.toEntity())

    suspend fun deleteCollection(collection: Collection) =
        collectionDao.deleteCollection(collection.toEntity())

    suspend fun addProjectToCollection(projectId: Long, collectionId: Long) =
        collectionDao.insertProjectCollectionCrossRef(
            ProjectCollectionCrossRef(projectId, collectionId)
        )

    suspend fun removeProjectFromCollection(projectId: Long, collectionId: Long) =
        collectionDao.removeProjectFromCollection(projectId, collectionId)
}

private fun CollectionEntity.toDomain() = Collection(
    id = id,
    name = name,
    description = description,
    color = color,
    createdDate = createdDate
)

private fun Collection.toEntity() = CollectionEntity(
    id = id,
    name = name,
    description = description,
    color = color,
    createdDate = createdDate
)
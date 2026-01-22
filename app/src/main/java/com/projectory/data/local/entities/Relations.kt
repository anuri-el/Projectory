package com.projectory.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ProjectWithCollections(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectCollectionCrossRef::class,
            parentColumn = "projectId",
            entityColumn = "collectionId"
        )
    )
    val collections: List<CollectionEntity>
)

data class ProjectWithTasks(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val tasks: List<TaskEntity>
)

data class CollectionWithProjects(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectCollectionCrossRef::class,
            parentColumn = "collectionId",
            entityColumn = "projectId"
        )
    )
    val projects: List<ProjectEntity>
)
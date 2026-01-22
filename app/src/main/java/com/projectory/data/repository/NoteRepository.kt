package com.projectory.data.repository

import com.projectory.data.local.dao.NoteDao
import com.projectory.data.local.entities.NoteEntity
import com.projectory.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getNotesForProject(projectId: Long): Flow<List<Note>> =
        noteDao.getNotesForProject(projectId).map { it.map { entity -> entity.toDomain() } }

    fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { it.map { entity -> entity.toDomain() } }

    suspend fun insertNote(note: Note): Long =
        noteDao.insertNote(note.toEntity())

    suspend fun updateNote(note: Note) =
        noteDao.updateNote(note.toEntity())

    suspend fun deleteNote(note: Note) =
        noteDao.deleteNote(note.toEntity())
}

private fun NoteEntity.toDomain() = Note(
    id = id,
    projectId = projectId,
    content = content,
    createdDate = createdDate,
    imagePaths = if (imagePaths.isNotEmpty()) imagePaths.split(",") else emptyList()
)

private fun Note.toEntity() = NoteEntity(
    id = id,
    projectId = projectId,
    content = content,
    createdDate = createdDate,
    imagePaths = imagePaths.joinToString(",")
)
package com.projectory.ui.project.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.title ?: "Project") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showEditProjectDialog() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { viewModel.showAddNoteDialog() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Note, contentDescription = "Add Note")
                }
                FloatingActionButton(
                    onClick = { viewModel.showAddTaskDialog() }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Project Header
                item {
                    ProjectHeaderCard(
                        project = uiState.project,
                        onStatusChange = { viewModel.updateProjectStatus(it) }
                    )
                }

                // Progress Card
                item {
                    ProgressCard(
                        progress = uiState.project?.progress ?: 0f,
                        totalTasks = uiState.tasks.size,
                        completedTasks = uiState.tasks.count { it.isCompleted }
                    )
                }

                // Tasks Section
                item {
                    Text(
                        "Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.tasks.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Default.CheckBox,
                            message = "No tasks yet",
                            subtitle = "Add tasks to track your progress"
                        )
                    }
                } else {
                    items(uiState.tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }

                // Notes Section
                item {
                    Text(
                        "Notes & Memories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.notes.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Default.Description,
                            message = "No notes yet",
                            subtitle = "Add notes to remember your journey"
                        )
                    }
                } else {
                    items(uiState.notes) { note ->
                        NoteItem(
                            note = note,
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { viewModel.hideAddTaskDialog() },
            onConfirm = { title -> viewModel.addTask(title) }
        )
    }

    if (uiState.showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { viewModel.hideAddNoteDialog() },
            onConfirm = { content -> viewModel.addNote(content) }
        )
    }
}

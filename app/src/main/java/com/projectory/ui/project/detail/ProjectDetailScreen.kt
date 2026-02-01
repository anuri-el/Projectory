package com.projectory.ui.project.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projectory.domain.model.ProjectStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "View History")
                    }
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
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
                    ProjectHeaderCard(project = uiState.project)
                }

                // Start Project Button (only for PLANNED projects)
                if (uiState.project?.status == ProjectStatus.PLANNED) {
                    item {
                        Button(
                            onClick = { viewModel.startProject() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Start Project",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.tasks.isNotEmpty()) {
                            Text(
                                "${uiState.tasks.count { it.isCompleted }}/${uiState.tasks.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
                    itemsIndexed(uiState.tasks) { index, task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { newTitle -> viewModel.editTask(task, newTitle) },
                            onMoveUp = if (index > 0 && !task.isCompleted) {
                                { viewModel.moveTaskUp(index) }
                            } else null,
                            onMoveDown = if (index < uiState.tasks.size - 1 && !task.isCompleted) {
                                { viewModel.moveTaskDown(index) }
                            } else null
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
                    items(uiState.notes.size) { index ->
                        NoteItem(
                            note = uiState.notes[index],
                            onDelete = { viewModel.deleteNote(uiState.notes[index]) }
                        )
                    }
                }
            }
        }
    }

    // Delete Project Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Project?") },
            text = {
                Text(
                    "Are you sure you want to delete \"${uiState.project?.title}\"? " +
                            "This will permanently delete all tasks, notes, and history. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject()
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
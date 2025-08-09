package com.example.myapplication.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.domain.model.TaskCategory
import com.example.myapplication.presentation.ui.components.TaskList
import com.example.myapplication.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val tomorrowTasks by viewModel.tomorrowTasks.collectAsStateWithLifecycle()
    val somedayTasks by viewModel.somedayTasks.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("明日から本気出す") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "タスクを追加"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                TaskCategory.values().forEach { category ->
                    Tab(
                        selected = selectedTab == category,
                        onClick = { viewModel.selectTab(category) },
                        text = { 
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }
            
            val tasks = when (selectedTab) {
                TaskCategory.TODAY -> todayTasks
                TaskCategory.TOMORROW -> tomorrowTasks
                TaskCategory.SOMEDAY -> somedayTasks
            }
            
            TaskList(
                tasks = tasks,
                category = selectedTab,
                onTaskClick = { task -> onNavigateToEditTask(task.id) },
                onCompleteTask = viewModel::completeTask,
                onMoveTask = viewModel::moveTask,
                onDeleteTask = viewModel::deleteTask,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
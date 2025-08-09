package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Task
import com.example.myapplication.domain.model.TaskCategory
import com.example.myapplication.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _selectedTab = MutableStateFlow(TaskCategory.TODAY)
    val selectedTab: StateFlow<TaskCategory> = _selectedTab.asStateFlow()
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    val todayTasks = taskRepository.getTasksByCategory(TaskCategory.TODAY)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val tomorrowTasks = taskRepository.getTasksByCategory(TaskCategory.TOMORROW)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val somedayTasks = taskRepository.getTasksByCategory(TaskCategory.SOMEDAY)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun selectTab(category: TaskCategory) {
        _selectedTab.value = category
    }
    
    fun completeTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.completeTask(task.id)
                _uiState.update { it.copy(
                    successMessage = "タスクを完了しました"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "タスクの完了に失敗しました"
                )}
            }
        }
    }
    
    fun moveTask(taskId: String, newCategory: TaskCategory) {
        viewModelScope.launch {
            try {
                taskRepository.moveTaskToCategory(taskId, newCategory)
                _uiState.update { it.copy(
                    successMessage = "タスクを${newCategory.displayName}に移動しました"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "タスクの移動に失敗しました"
                )}
            }
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTaskById(taskId)
                _uiState.update { it.copy(
                    successMessage = "タスクを削除しました"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "タスクの削除に失敗しました"
                )}
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(
            successMessage = null,
            errorMessage = null
        )}
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Priority
import com.example.myapplication.domain.model.Task
import com.example.myapplication.domain.model.TaskCategory
import com.example.myapplication.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: String? = savedStateHandle.get<String>("taskId")
    
    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()
    
    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }
    
    private fun loadTask(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = taskRepository.getTaskById(id)
                if (task != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            title = task.title,
                            description = task.description,
                            priority = task.priority,
                            category = task.category,
                            isEditMode = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "タスクが見つかりません"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "タスクの読み込みに失敗しました"
                )}
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }
    
    fun updateCategory(category: TaskCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun saveTask(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "タイトルを入力してください") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                if (state.isEditMode && taskId != null) {
                    val updatedTask = Task(
                        id = taskId,
                        title = state.title.trim(),
                        description = state.description.trim(),
                        priority = state.priority,
                        category = state.category,
                        updatedAt = System.currentTimeMillis()
                    )
                    taskRepository.updateTask(updatedTask)
                } else {
                    val newTask = Task(
                        title = state.title.trim(),
                        description = state.description.trim(),
                        priority = state.priority,
                        category = state.category
                    )
                    taskRepository.insertTask(newTask)
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    errorMessage = if (state.isEditMode) "タスクの更新に失敗しました" else "タスクの保存に失敗しました"
                )}
            }
        }
    }
}

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.SOMEDAY,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val titleError: String? = null,
    val errorMessage: String? = null
)
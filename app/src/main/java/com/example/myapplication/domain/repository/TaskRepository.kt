package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Task
import com.example.myapplication.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    fun getAllActiveTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: String)
    suspend fun moveTaskToCategory(taskId: String, newCategory: TaskCategory)
    suspend fun completeTask(taskId: String)
}
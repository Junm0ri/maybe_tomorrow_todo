package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.local.dao.TaskDao
import com.example.myapplication.data.mapper.toDomainModel
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.Task
import com.example.myapplication.domain.model.TaskCategory
import com.example.myapplication.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    
    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> {
        return taskDao.getTasksByCategory(category)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override fun getAllActiveTasks(): Flow<List<Task>> {
        return taskDao.getAllActiveTasks()
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun getTaskById(id: String): Task? {
        return try {
            taskDao.getTaskById(id)?.toDomainModel()
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to get task by id", e)
            null
        }
    }
    
    override suspend fun insertTask(task: Task) {
        try {
            taskDao.insertTask(task.toEntity())
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to insert task", e)
            throw TaskInsertException("タスクの保存に失敗しました", e)
        }
    }
    
    override suspend fun updateTask(task: Task) {
        try {
            taskDao.updateTask(task.toEntity())
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to update task", e)
            throw TaskUpdateException("タスクの更新に失敗しました", e)
        }
    }
    
    override suspend fun deleteTask(task: Task) {
        try {
            taskDao.deleteTask(task.toEntity())
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to delete task", e)
            throw TaskDeleteException("タスクの削除に失敗しました", e)
        }
    }
    
    override suspend fun deleteTaskById(taskId: String) {
        try {
            taskDao.deleteTaskById(taskId)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to delete task by id", e)
            throw TaskDeleteException("タスクの削除に失敗しました", e)
        }
    }
    
    override suspend fun moveTaskToCategory(taskId: String, newCategory: TaskCategory) {
        try {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val updatedTask = task.copy(
                    category = newCategory,
                    updatedAt = System.currentTimeMillis()
                )
                taskDao.updateTask(updatedTask)
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to move task to category", e)
            throw TaskUpdateException("タスクの移動に失敗しました", e)
        }
    }
    
    override suspend fun completeTask(taskId: String) {
        try {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val completedTask = task.copy(
                    isCompleted = true,
                    updatedAt = System.currentTimeMillis()
                )
                taskDao.updateTask(completedTask)
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to complete task", e)
            throw TaskUpdateException("タスクの完了に失敗しました", e)
        }
    }
}

class TaskInsertException(message: String, cause: Throwable) : Exception(message, cause)
class TaskUpdateException(message: String, cause: Throwable) : Exception(message, cause)
class TaskDeleteException(message: String, cause: Throwable) : Exception(message, cause)
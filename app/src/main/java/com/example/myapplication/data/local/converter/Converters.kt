package com.example.myapplication.data.local.converter

import androidx.room.TypeConverter
import com.example.myapplication.domain.model.Priority
import com.example.myapplication.domain.model.TaskCategory

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)

    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(category: String): TaskCategory = TaskCategory.valueOf(category)
}
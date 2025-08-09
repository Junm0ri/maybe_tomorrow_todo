package com.example.myapplication.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Task
import com.example.myapplication.domain.model.TaskCategory

@Composable
fun TaskList(
    tasks: List<Task>,
    category: TaskCategory,
    onTaskClick: (Task) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onMoveTask: (String, TaskCategory) -> Unit,
    onDeleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        EmptyTaskList(category = category, modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = onTaskClick,
                    onCompleteClick = onCompleteTask,
                    onMoveTask = onMoveTask,
                    onDeleteClick = onDeleteTask
                )
            }
        }
    }
}

@Composable
fun EmptyTaskList(
    category: TaskCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (category) {
                    TaskCategory.TODAY -> "今日のタスクはありません"
                    TaskCategory.TOMORROW -> "明日のタスクはありません"
                    TaskCategory.SOMEDAY -> "いつかやるタスクはありません"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "＋ボタンから新しいタスクを追加しましょう",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
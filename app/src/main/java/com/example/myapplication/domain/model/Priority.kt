package com.example.myapplication.domain.model

import androidx.compose.ui.graphics.Color

enum class Priority(val displayName: String, val color: Color) {
    HIGH("高", Color.Red),
    MEDIUM("中", Color.Yellow),
    LOW("低", Color.Green)
}
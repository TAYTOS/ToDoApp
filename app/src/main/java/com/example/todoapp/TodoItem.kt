package com.example.todoapp

import java.util.UUID

data class TodoItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)

enum class Priority { LOW, MEDIUM, HIGH }

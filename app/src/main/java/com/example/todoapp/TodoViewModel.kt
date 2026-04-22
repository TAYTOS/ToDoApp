package com.example.todoapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val inputText: String = "",
    val selectedPriority: Priority = Priority.MEDIUM,
    val editingId: String? = null,
    val editingText: String = ""
)

class TodoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.value = _uiState.value.copy(selectedPriority = priority)
    }

    fun addTodo() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        _uiState.value = _uiState.value.copy(
            todos = _uiState.value.todos + TodoItem(
                text = text,
                priority = _uiState.value.selectedPriority
            ),
            inputText = "",
            selectedPriority = Priority.MEDIUM
        )
    }

    fun toggleComplete(id: String) {
        _uiState.value = _uiState.value.copy(
            todos = _uiState.value.todos.map {
                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
            }
        )
    }

    fun startEditing(id: String) {
        val todo = _uiState.value.todos.find { it.id == id } ?: return
        _uiState.value = _uiState.value.copy(editingId = id, editingText = todo.text)
    }

    fun onEditTextChange(text: String) {
        _uiState.value = _uiState.value.copy(editingText = text)
    }

    fun confirmEdit() {
        val id = _uiState.value.editingId ?: return
        val newText = _uiState.value.editingText.trim()
        if (newText.isBlank()) return
        _uiState.value = _uiState.value.copy(
            todos = _uiState.value.todos.map {
                if (it.id == id) it.copy(text = newText) else it
            },
            editingId = null,
            editingText = ""
        )
    }

    fun cancelEdit() {
        _uiState.value = _uiState.value.copy(editingId = null, editingText = "")
    }

    fun deleteTodo(id: String) {
        _uiState.value = _uiState.value.copy(
            todos = _uiState.value.todos.filter { it.id != id }
        )
    }
}
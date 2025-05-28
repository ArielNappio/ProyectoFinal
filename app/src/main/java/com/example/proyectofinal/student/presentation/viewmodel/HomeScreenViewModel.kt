package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.data.repository.TaskRepository
import com.example.proyectofinal.student.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel(
    private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun toggleFavorite(taskId: Int) {
        repository.toggleFavorite(taskId)
    }

}
package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.data.repository.TaskRepository
import com.example.proyectofinal.student.domain.model.Task
import kotlinx.coroutines.flow.StateFlow

class HomeScreenViewModel(
    private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()

    fun toggleFavorite(taskId: Int) {
        repository.toggleFavorite(taskId)
    }

}
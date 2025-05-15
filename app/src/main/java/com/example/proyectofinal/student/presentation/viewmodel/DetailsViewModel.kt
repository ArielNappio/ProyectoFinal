package com.example.proyectofinal.student.presentation.viewmodel

import com.example.proyectofinal.student.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.data.repository.TaskRepository
import kotlinx.coroutines.flow.asStateFlow

class DetailsViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    fun getNoteById(noteId: Int) {
        val foundNote = repository.getTaskById(noteId)
        println("Buscando nota con id $noteId, encontrada: $foundNote")
        _task.value = foundNote
    }

}

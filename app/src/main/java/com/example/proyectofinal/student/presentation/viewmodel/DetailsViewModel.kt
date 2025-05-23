package com.example.proyectofinal.student.presentation.viewmodel

import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.student.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailsViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    val _fontSizeTitle = MutableStateFlow(26.sp)
    val fontSizeTitle = _fontSizeTitle.asStateFlow()

    val _fontSizeText = MutableStateFlow(22.sp)
    val fontSizeText = _fontSizeText.asStateFlow()

    fun getTaskById(taskId: Int) {
        val foundTask = repository.getTaskById(taskId)
        println("Buscando nota con id $taskId, encontrada: $foundTask")
        _task.value = foundTask
    }

}

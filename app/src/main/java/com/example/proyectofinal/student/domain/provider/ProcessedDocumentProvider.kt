package com.example.proyectofinal.student.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.data.remoteData.model.Task
import kotlinx.coroutines.flow.Flow

interface ProcessedDocumentProvider {
    fun getTaskById(id: Int): Flow<NetworkResponse<Task>>
    fun getAllTasks(): Flow<NetworkResponse<List<Task>>>
    fun postTask(task: Task): Flow<NetworkResponse<Task>>
}
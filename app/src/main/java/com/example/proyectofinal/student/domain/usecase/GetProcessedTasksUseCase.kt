package com.example.proyectofinal.student.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.data.remoteData.model.Task
import com.example.proyectofinal.student.domain.provider.ProcessedDocumentProvider
import kotlinx.coroutines.flow.Flow

class GetProcessedTasksUseCase(private val processedDocument: ProcessedDocumentProvider) {
    operator fun invoke(): Flow<NetworkResponse<List<Task>>> = processedDocument.getAllTasks()
}
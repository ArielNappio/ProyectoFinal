package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _projectState = MutableStateFlow<NetworkResponse<OrderDelivered>>(NetworkResponse.Loading())
    val projectState: StateFlow<NetworkResponse<OrderDelivered>> = _projectState.asStateFlow()

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            try {
                // Obtener el userId real del TokenManager
                val userId = tokenManager.userId.first()
                if (userId != null) {
                    // Consultar la base de datos Room a través del use case
                    getOrders(userId).collect { response ->
                        when (response) {
                            is NetworkResponse.Success -> {
                                // Buscar el proyecto específico por ID
                                val project = response.data?.find { it.id == projectId }
                                if (project != null) {
                                    _projectState.value = NetworkResponse.Success(project)
                                } else {
                                    _projectState.value = NetworkResponse.Failure("Proyecto no encontrado")
                                }
                            }
                            is NetworkResponse.Failure -> {
                                _projectState.value = NetworkResponse.Failure(response.error)
                            }
                            is NetworkResponse.Loading -> {
                                _projectState.value = NetworkResponse.Loading()
                            }
                        }
                    }
                } else {
                    _projectState.value = NetworkResponse.Failure("Usuario no autenticado")
                }
            } catch (e: Exception) {
                _projectState.value = NetworkResponse.Failure(e.message ?: "Error desconocido")
            }
        }
    }
}

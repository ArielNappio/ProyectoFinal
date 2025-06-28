package com.example.proyectofinal.student.presentation.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val repository: UserPreferencesRepository,
    private val lastReadRepository: LastReadRepository
) : ViewModel() {

    private val _projectState =
        MutableStateFlow<NetworkResponse<OrderDelivered>>(NetworkResponse.Loading())
    val projectState: StateFlow<NetworkResponse<OrderDelivered>> = _projectState.asStateFlow()

    private val _iconSize = MutableStateFlow(24.dp)
    val iconSize: StateFlow<Dp> = _iconSize.asStateFlow()

    private val _fontSize = MutableStateFlow(24.dp)
    val fontSize: StateFlow<Dp> = _fontSize.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserPreferences().collect { prefs ->
                _iconSize.emit(prefs.iconSize.dp)
                _fontSize.emit(prefs.fontSize.dp)
            }
        }
    }

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
                                    val updatedOrders = project.orders.map { task ->
                                        val lastReadInt =
                                            lastReadRepository.getLastReadPage(task.id)
                                        task.copy(lastRead = lastReadInt.toString())
                                    }

                                    _projectState.emit(
                                        NetworkResponse.Success(
                                            project.copy(orders = updatedOrders)
                                        )
                                    )
                                } else {
                                    _projectState.emit(NetworkResponse.Failure("Proyecto no encontrado"))
                                }
                            }

                            is NetworkResponse.Failure -> {
                                _projectState.emit(NetworkResponse.Failure(response.error))
                            }

                            is NetworkResponse.Loading -> {
                                _projectState.emit(NetworkResponse.Loading())
                            }
                        }
                    }
                } else {
                    _projectState.emit(NetworkResponse.Failure("Usuario no autenticado"))
                }
            } catch (e: Exception) {
                _projectState.emit(NetworkResponse.Failure(e.message ?: "Error desconocido"))
            }
        }
    }


    fun toggleFavorite(orderId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            updateFavoriteStatusUseCase(orderId, isFavorite)
            val currentProject = (projectState.value as? NetworkResponse.Success)?.data
            if (currentProject != null && currentProject.id == orderId) {
                _projectState.emit(
                    NetworkResponse.Success(
                        currentProject.copy(isFavorite = isFavorite)
                    )
                )
            }
        }
    }
}

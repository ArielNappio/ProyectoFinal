package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.domain.usecase.GetOrdersManagmentUseCase
import com.example.proyectofinal.student.data.repository.TaskRepository
import com.example.proyectofinal.student.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val repository: TaskRepository,
    private val getOrdersManagment: GetOrdersManagmentUseCase
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _orderManagmentState = MutableStateFlow<NetworkResponse<List<Task>>>(NetworkResponse.Loading())
    val orderManagmentState: StateFlow<NetworkResponse<List<Task>>> = _orderManagmentState.asStateFlow()

    init {
        getOrdersManagments("6fe4e631-769c-4019-b900-d61dda1689e9") // tu studentId real
    }

    private fun getOrdersManagments(studentId: String) {
        viewModelScope.launch {
            getOrdersManagment(studentId).collect { response ->
                _orderManagmentState.value = response
            }
        }
    }


    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun toggleFavorite(taskId: Int) {
        // repository.toggleFavorite(taskId)
    }
}

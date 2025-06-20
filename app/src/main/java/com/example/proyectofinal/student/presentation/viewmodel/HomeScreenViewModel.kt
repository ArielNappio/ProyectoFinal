package com.example.proyectofinal.student.presentation.viewmodel

import android.util.Log
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

class HomeScreenViewModel(
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _orderManagementState = MutableStateFlow<NetworkResponse<List<OrderDelivered>>>(NetworkResponse.Loading())
    val orderManagementState: StateFlow<NetworkResponse<List<OrderDelivered>>> = _orderManagementState.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderDelivered>>(emptyList())
    val orders: StateFlow<List<OrderDelivered>> = _orders.asStateFlow()

    init {
        getOrdersManagements()
    }

    private fun getOrdersManagements() {
        viewModelScope.launch {
            val userId = tokenManager.userId.first()
            if (userId != null) {
                getOrders(userId).collect { response ->
                    _orderManagementState.value = response
                    _orders.value = response.data ?: emptyList()
                    println("DEBUG JSON: $response")
                }
                Log.d("Home", "User ID: $userId")
            } else {
                Log.e("Home", "User ID is null")
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

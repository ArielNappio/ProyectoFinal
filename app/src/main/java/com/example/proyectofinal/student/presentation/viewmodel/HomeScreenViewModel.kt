package com.example.proyectofinal.student.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
    ) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderManagementState = MutableStateFlow<NetworkResponse<List<OrderDelivered>>>(NetworkResponse.Loading())
    val orderManagementState: StateFlow<NetworkResponse<List<OrderDelivered>>> = _orderManagementState.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderDelivered>>(emptyList())
    val orders: StateFlow<List<OrderDelivered>> = _orders.asStateFlow()

    init {
            getOrdersManagements()
    }

    private fun getOrdersManagements() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.userId.first()
            if (userId != null) {
                getOrders(userId).collect { response ->
                    if (response is NetworkResponse.Success) {
                        Log.d("VM", "Received orders: ${response.data?.joinToString { it.id + " fav:" + it.isFavorite }}")
                    }
                    _orderManagementState.value = response
                    _orders.value = response.data ?: emptyList()
                    println("DEBUG JSON: $response")
                }
                Log.d("Home", "User ID: $userId")
            } else {
                Log.e("Home", "User ID is null")
            }
            _isLoading.value = false
        }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun toggleFavorite(orderId: String, isFavorite: Boolean) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(isFavorite = isFavorite)
            else it
        }
        viewModelScope.launch {
            updateFavoriteStatusUseCase(orderId, isFavorite)
        }
    }
}

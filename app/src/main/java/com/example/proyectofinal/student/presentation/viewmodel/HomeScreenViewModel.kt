package com.example.proyectofinal.student.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagment.domain.usecase.GetOrdersManagmentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val getOrdersManagment: GetOrdersManagmentUseCase,
    private val tokenManager: TokenManager,
    ) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _orderManagmentState = MutableStateFlow<NetworkResponse<List<OrderDelivered>>>(NetworkResponse.Loading())
    val orderManagmentState: StateFlow<NetworkResponse<List<OrderDelivered>>> = _orderManagmentState.asStateFlow()

    init {
        getOrdersManagments()
    }

    private fun getOrdersManagments() {
        viewModelScope.launch {
            val userId = tokenManager.userId.first()
            if (userId != null) {
                getOrdersManagment(userId).collect { response ->
                    _orderManagmentState.value = response
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

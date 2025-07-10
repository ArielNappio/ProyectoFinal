package com.example.proyectofinal.order.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.order.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrderByIdUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.order.domain.usecase.UpdateOrderUseCase
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private fun fetchOrders() {
        viewModelScope.launch {
            getOrdersUseCase().collect {
                when (it) {
                    is NetworkResponse.Failure<*> -> _orders.value = it.data ?: emptyList()
                    is NetworkResponse.Loading<*> -> Log.e("OrderViewModel", "Error: ${it.error}")
                    is NetworkResponse.Success<*> -> Log.d("OrderViewModel", "Cargando...")
                }
            }
        }
    }

    fun getOrderById(id: Int) {
        viewModelScope.launch {
            getOrderByIdUseCase(id).collect {
                when (it) {
                    is NetworkResponse.Success -> _selectedOrder.value = it.data
                    is NetworkResponse.Failure -> Log.e("OrderViewModel", "Error: ${it.error}")
                    is NetworkResponse.Loading -> Log.d("OrderViewModel", "Cargando order $id...")
                }
            }
        }
    }

    fun createOrder(order: Order) {
        viewModelScope.launch {
            createOrderUseCase(order).collect {
                fetchOrders()
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            updateOrderUseCase(order).collect {
                fetchOrders()
            }
        }
    }

    fun deleteOrder(id: Int) {
        viewModelScope.launch {
            deleteOrderUseCase(id).collect {
                fetchOrders()
            }
        }
    }

}
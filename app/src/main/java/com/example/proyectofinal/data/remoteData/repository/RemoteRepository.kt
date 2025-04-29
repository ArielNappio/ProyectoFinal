package com.example.proyectofinal.data.remoteData.repository

import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.data.remoteData.model.LoginRequest
import com.example.proyectofinal.data.remoteData.model.LoginResponse
import com.example.proyectofinal.data.remoteData.model.Order
import com.example.proyectofinal.util.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface RemoteRepository{
    fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>>
    fun getItem(): Flow<NetworkResponse<List<Item>>>

    //orders
    fun getOrders(): Flow<NetworkResponse<List<Order>>>
    fun getOrderById(id: Int): Flow<NetworkResponse<Order>>
    fun createOrder(order: Order): Flow<NetworkResponse<Order>>
    fun updateOrder(order: Order): Flow<NetworkResponse<Unit>>
    fun deleteOrder(id: Int): Flow<NetworkResponse<Unit>>

}
package com.example.proyectofinal.data.remoteData.repository

import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.data.remoteData.model.LoginRequest
import com.example.proyectofinal.data.remoteData.model.LoginResponse
import com.example.proyectofinal.util.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface RemoteRepository{
    fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>>
    fun getItem(): Flow<NetworkResponse<List<Item>>>
}
package com.example.proyectofinal.users.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.users.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserProvider{

fun getUsers(): Flow<NetworkResponse<List<User>>>
fun deleteUser(id: String) : Flow<NetworkResponse<Unit>>


}
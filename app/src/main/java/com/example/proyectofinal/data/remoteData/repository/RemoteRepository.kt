package com.example.proyectofinal.data.remoteData.repository

import android.net.Network
import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.util.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface RemoteRepository{
    fun postItem(): Any?
    fun getItem(): Flow<NetworkResponse<List<Item>>>
}
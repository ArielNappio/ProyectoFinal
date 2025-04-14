package com.example.proyectofinal.data.remoteData.repository

import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.util.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.internal.connection.Exchange

class RemoteRepoImpl (
    private val ktorClient: HttpClient
): RemoteRepository{
    override fun postItem(): Any? {
        TODO("Not yet implemented")
    }

    override fun getItem(): Flow<NetworkResponse<List<Item>>> = flow{
        try{
            emit(NetworkResponse.Loading())

            val request = ktorClient.prepareGet(ApiUrls.TEST)
            val response = request.execute()
            val responseBody = response.bodyAsText()
            val forecasts = Json.decodeFromString<List<Item>>(responseBody)
            emit(NetworkResponse.Success(data = forecasts))
        }
        catch (e: Exception){
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }


}
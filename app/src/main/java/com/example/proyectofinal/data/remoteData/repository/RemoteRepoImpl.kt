package com.example.proyectofinal.data.remoteData.repository

import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.data.remoteData.model.LoginRequest
import com.example.proyectofinal.util.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import com.example.proyectofinal.data.remoteData.model.LoginResponse
import com.example.proyectofinal.data.remoteData.model.UserResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType


class RemoteRepoImpl (
    private val ktorClient: HttpClient
): RemoteRepository{

    override fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val response = ktorClient.post(ApiUrls.LOGIN) {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(loginRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<LoginResponse>()
                emit(NetworkResponse.Success(data = responseBody))
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "POST: Unknown error"))
        }
    }

    override fun getMe(token: String): Flow<NetworkResponse<UserResponse>> = flow {

        try{
            val response = ktorClient.get(ApiUrls.AUTH_ME) {
                header("Authorization", "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                val user = response.body<UserResponse>()
                emit(NetworkResponse.Success(user))
            } else {
                emit(NetworkResponse.Failure("Error: ${response.status.description}"))
            }
        }
        catch (e: Exception){
            emit(NetworkResponse.Failure(error = e.toString()))
        }

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
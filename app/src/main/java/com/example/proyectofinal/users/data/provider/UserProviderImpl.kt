package com.example.proyectofinal.users.data.provider

import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.domain.provider.UserProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserProviderImpl(
    private val ktorClient: HttpClient
) : UserProvider {

    override fun getUsers(): Flow<NetworkResponse<List<User>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = ktorClient.get(ApiUrls.USER)
            if (response.status == HttpStatusCode.Companion.OK) {
                val responseBody = response.body<List<User>>()
                emit(NetworkResponse.Success(data = responseBody))
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun deleteUser(id: Int): Flow<NetworkResponse<Unit>> = flow{
            try {
                emit(NetworkResponse.Loading())
                val response = ktorClient.delete("${ApiUrls.USER}/$id")
                if (response.status == HttpStatusCode.Companion.OK) {
                    emit(NetworkResponse.Success(data = Unit))
                }
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(error = e.toString()))
            }
        }
}
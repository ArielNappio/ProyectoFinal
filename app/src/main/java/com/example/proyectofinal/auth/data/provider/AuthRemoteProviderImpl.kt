package com.example.proyectofinal.auth.data.provider

import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRemoteProviderImpl (
   private val ktorClient: HttpClient,
    private val tokenManager: TokenManager
): AuthRemoteProvider {
   override fun postLogin(loginRequestDto: LoginRequestDto): Flow<NetworkResponse<LoginResponseDto>> = flow {
       try {
           emit(NetworkResponse.Loading())

           val response = ktorClient.post(ApiUrls.LOGIN) {
               contentType(ContentType.Application.Json)
               setBody(loginRequestDto)
           }

           if (response.status == HttpStatusCode.Companion.OK) {
               val responseBody = response.body<LoginResponseDto>()
               emit(NetworkResponse.Success(data = responseBody))
           } else {
               emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
           }
       } catch (e: Exception) {
           emit(NetworkResponse.Failure(error = e.localizedMessage ?: "POST: Unknown error"))
       }
   }

    override fun getMe(): Flow<NetworkResponse<UserResponseDto>> = flow {
        try {
            val userId = tokenManager.userId.first()

            if (userId.isNullOrEmpty()) {
                emit(NetworkResponse.Failure("userId no encontrado"))
                return@flow
            }

            val response = ktorClient.get(ApiUrls.GET_ME.replace("{id}", userId))

            if (response.status == HttpStatusCode.OK) {
                val user = response.body<UserResponseDto>()
                emit(NetworkResponse.Success(user))
            } else {
                emit(NetworkResponse.Failure("Error: ${response.status.description}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure("Excepción: ${e.localizedMessage ?: e.toString()}"))
        }
    }

}
package com.example.proyectofinal.auth.data.remoteData.repository

import com.example.proyectofinal.auth.data.remoteData.model.LoginRequest
import com.example.proyectofinal.auth.data.remoteData.model.LoginResponse
import com.example.proyectofinal.auth.data.remoteData.model.UserResponse
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRemoteRepoImpl (
   private val ktorClient: HttpClient
): AuthRemoteRepository {
   override fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>> = flow {
       try {
           emit(NetworkResponse.Loading())

           val response = ktorClient.post(ApiUrls.LOGIN) {
               contentType(ContentType.Application.Json)
               setBody(loginRequest)
           }

           if (response.status == HttpStatusCode.Companion.OK) {
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

       try {
           val response = ktorClient.get(ApiUrls.AUTH_ME) {
               header("Authorization", "Bearer $token")
           }
           if (response.status == HttpStatusCode.Companion.OK) {
               val user = response.body<UserResponse>()
               emit(NetworkResponse.Success(user))
           } else {
               emit(NetworkResponse.Failure("Error: ${response.status.description}"))
           }
       } catch (e: Exception) {
           emit(NetworkResponse.Failure(error = e.toString()))
       }

   }

}
package com.example.proyectofinal.users.data.provider

import android.util.Log
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.domain.provider.UserProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodeURLPath
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


    override fun deleteUser(id: String): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())

            // Codificamos el ID para que sea seguro en la URL (por ejemplo, @ → %40)
            val encodedId = id.encodeURLPath()

            // Nos aseguramos de que la URL no termine con / antes de concatenar el ID
            val url = "${ApiUrls.USER.trimEnd('/')}/$encodedId"

            // Log para verificar la URL final usada en el DELETE
            Log.d("DeleteUser", "URL final: $url")

            // Realizamos la solicitud DELETE
            val response = ktorClient.delete(url)

            // Verificamos el estado de la respuesta
            if (response.status == HttpStatusCode.OK) {
                emit(NetworkResponse.Success(data = Unit))
            } else {
                emit(
                    NetworkResponse.Failure(
                        error = "Status: ${response.status}, Body: ${response.bodyAsText()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun updateUser(id: String, updatedUser: User): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val encodedId = id.encodeURLPath()
            val url = "${ApiUrls.USER.trimEnd('/')}/$encodedId"

            Log.d("UpdateUser", "URL final: $url")

            val response = ktorClient.put(url) {
                contentType(ContentType.Application.Json)
                setBody(updatedUser)  // Asegurate que 'User' es serializable
            }

            if (response.status == HttpStatusCode.OK) {
                emit(NetworkResponse.Success(data = Unit))
            } else {
                emit(
                    NetworkResponse.Failure(
                        error = "Status: ${response.status}, Body: ${response.bodyAsText()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }

    }

    override fun createUser(createUser: User): Flow<NetworkResponse<User>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = ktorClient.post(ApiUrls.REGISTER) {   // Cambié USER por REGISTER
                contentType(ContentType.Application.Json)
                setBody(createUser)
            }

            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val createdUser = response.body<User>()
                emit(NetworkResponse.Success(createdUser))
            } else {
                emit(
                    NetworkResponse.Failure(
                        error = "Status: ${response.status}, Body: ${response.bodyAsText()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }
}
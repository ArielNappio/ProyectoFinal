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
import com.example.proyectofinal.data.remoteData.model.Order
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType


class RemoteRepoImpl(
    private val ktorClient: HttpClient
) : RemoteRepository {
    override fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>> =
        flow {
            try {
                emit(NetworkResponse.Loading())

                val response = ktorClient.post(ApiUrls.LOGIN) {
                    contentType(ContentType.Application.Json)
                    setBody(loginRequest)
                }

                if (response.status == HttpStatusCode.OK) {
                    val responseBody = response.body<LoginResponse>()
                    emit(NetworkResponse.Success(data = responseBody))
                } else {
                    emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
                }
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
            }
        }

    override fun getItem(): Flow<NetworkResponse<List<Item>>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val request = ktorClient.prepareGet(ApiUrls.TEST)
            val response = request.execute()
            val responseBody = response.bodyAsText()
            val forecasts = Json.decodeFromString<List<Item>>(responseBody)
            emit(NetworkResponse.Success(data = forecasts))
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun getOrders(): Flow<NetworkResponse<List<Order>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = ktorClient.get(ApiUrls.ORDERS)
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<List<Order>>()
                emit(NetworkResponse.Success(data = responseBody))
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun getOrderById(id: Int): Flow<NetworkResponse<Order>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.get("${ApiUrls.ORDER}/$id")
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<Order>()
                emit(NetworkResponse.Success(data = responseBody))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun createOrder(order: Order): Flow<NetworkResponse<Order>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.post(ApiUrls.ORDERS) {
                contentType(ContentType.Application.Json)
                setBody(order)
            }
            if (response.status == HttpStatusCode.Created) {
                emit(NetworkResponse.Success(data = order))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun updateOrder(order: Order): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.put("${ApiUrls.ORDER}/${order.id}"){
                contentType(ContentType.Application.Json)
                setBody(order)
            }
            if (response.status == HttpStatusCode.OK) {
                emit(NetworkResponse.Success(data = Unit))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun deleteOrder(id: Int): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.delete("${ApiUrls.ORDER}/$id")
            if (response.status == HttpStatusCode.OK) {
                emit(NetworkResponse.Success(data = Unit))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

}



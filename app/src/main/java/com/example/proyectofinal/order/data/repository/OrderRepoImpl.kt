package com.example.proyectofinal.order.data.repository

import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepoImpl(
    private val ktorClient: HttpClient
): OrderRepository {

    override fun getOrders(): Flow<NetworkResponse<List<Order>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = ktorClient.get(ApiUrls.ORDERS)
            if (response.status == HttpStatusCode.Companion.OK) {
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
            if (response.status == HttpStatusCode.Companion.OK) {
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
            if (response.status == HttpStatusCode.Companion.Created) {
                emit(NetworkResponse.Success(data = order))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun updateOrder(order: Order): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.put("${ApiUrls.ORDER}/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(order)
            }
            if (response.status == HttpStatusCode.Companion.OK) {
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
            if (response.status == HttpStatusCode.Companion.OK) {
                emit(NetworkResponse.Success(data = Unit))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

}
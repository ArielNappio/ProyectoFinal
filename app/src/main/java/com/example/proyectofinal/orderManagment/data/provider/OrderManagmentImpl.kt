package com.example.proyectofinal.orderManagment.data.provider

import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagment.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagment.domain.provider.OrderManagmentProvider
import com.example.proyectofinal.orderManagment.mapper.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class OrderManagmentImpl(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : OrderManagmentProvider {

    override fun getOrdersManagment(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>> = flow {
        emit(NetworkResponse.Loading())

        try {
            val token = tokenManager.token.firstOrNull()

            if (token.isNullOrEmpty()) {
                emit(NetworkResponse.Failure("No auth token found"))
                return@flow
            }

            val url = ApiUrls.ORDER_MANAGMENT.replace("{studentId}", studentId)
            val response = httpClient.get(url) {
                header("Authorization", "Bearer $token")
            }
            val bodyText = response.bodyAsText()
            println("DEBUG JSON: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<List<OrderDeliveredDto>>()
                val orderDelivered = responseBody.map { it.toDomain() }
                emit(NetworkResponse.Success(data = orderDelivered))
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }

        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }
}
package com.example.proyectofinal.orderManagement.data.provider

import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.mapper.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class OrderManagementImpl(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : OrderManagementProvider {

    override fun getOrdersManagement(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>> = flow {
        emit(NetworkResponse.Loading())

        try {
            val token = tokenManager.token.firstOrNull()

            if (token.isNullOrEmpty()) {
                emit(NetworkResponse.Failure("No auth token found"))
                return@flow
            }

            val url = ApiUrls.ORDERS_BY_STUDENT.replace("{studentId}", studentId)
            val response = httpClient.get(url) {
                header("Authorization", "Bearer $token")
            }
            val bodyText = response.bodyAsText()
            println("DEBUG JSON: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<List<OrderDeliveredDto>>()
                val orderDelivered = responseBody.map { it.toDomain() }
                emit(NetworkResponse.Success(data = orderDelivered))
                println("se supone que todo ok y q la trajo")
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }

        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }
}
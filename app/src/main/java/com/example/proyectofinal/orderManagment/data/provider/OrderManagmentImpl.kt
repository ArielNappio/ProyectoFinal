package com.example.proyectofinal.orderManagment.data.provider

import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.data.dto.OrderResponseDto
import com.example.proyectofinal.orderManagment.domain.provider.OrderManagmentProvider
import com.example.proyectofinal.orderManagment.mapper.toTask
import com.example.proyectofinal.student.domain.model.Task
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderManagmentImpl(
    private val httpClient: HttpClient
) : OrderManagmentProvider {

    override fun getOrdersManagment(studentId: String): Flow<NetworkResponse<List<Task>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val url = ApiUrls.ORDER_MANAGMENT.replace("{studentId}", studentId)
            val response = httpClient.get(url)
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<List<OrderResponseDto>>()
                val tasks = responseBody.map { it.toTask() }
                emit(NetworkResponse.Success(data = tasks))
            } else {
                emit(NetworkResponse.Failure(error = "Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.localizedMessage ?: "Unknown error"))
        }
    }
}
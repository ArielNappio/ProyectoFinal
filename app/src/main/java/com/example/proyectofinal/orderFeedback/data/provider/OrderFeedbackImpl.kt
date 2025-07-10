package com.example.proyectofinal.orderFeedback.data.provider

import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderFeedback.domain.model.OrderFeedback
import com.example.proyectofinal.orderFeedback.domain.provider.OrderFeedbackProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderFeedbackImpl(private val ktorClient: HttpClient) : OrderFeedbackProvider{
    override fun sendFeedback(feedback: OrderFeedback) : Flow<NetworkResponse<OrderFeedback>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.post(ApiUrls.ORDER_FEEDBACK) {
                contentType(ContentType.Application.Json)
                setBody(feedback)
            }
            if (response.status == HttpStatusCode.Companion.Created) {
                emit(NetworkResponse.Success(data = feedback))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }
}
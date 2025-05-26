package com.example.proyectofinal.mail.data.provider

import io.ktor.http.contentType
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MailProviderImpl(private val ktorClient: HttpClient) : MailProvider {
    override fun sendMessage(message: MessageModel): Flow<NetworkResponse<MessageModel>> = flow{
        try {
            emit(NetworkResponse.Loading())
            val response = ktorClient.post(ApiUrls.MESSAGE) {
                contentType(ContentType.Application.Json)
                setBody(message)
            }
            if (response.status == HttpStatusCode.Companion.Created) {
                emit(NetworkResponse.Success(data = message))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun receiveMessage(): Flow<NetworkResponse<MessageModel>> {
        TODO("Not yet implemented")
    }

    override fun saveDraft(): Flow<NetworkResponse<Unit>> {
        TODO("Not yet implemented")
    }

    override fun deleteDraft(): Flow<NetworkResponse<Unit>> {
        TODO("Not yet implemented")
    }
}
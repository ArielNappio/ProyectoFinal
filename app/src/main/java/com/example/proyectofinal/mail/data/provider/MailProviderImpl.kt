package com.example.proyectofinal.mail.data.provider

import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class MailProviderImpl(
    private val ktorClient: HttpClient,
    private val tokenManager: TokenManager
) : MailProvider {

    override fun sendMessage(message: MessageModel): Flow<NetworkResponse<MessageModel>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val formData = formData {
                append("userFromId", message.userFromId)
                append("userToId", message.userToId)
                append("isDraft", message.isDraft.toString())
                append("sender", message.sender)
                append("subject", message.subject)
                append("date", message.date)
                append("content", message.content)
                append("responded", message.isResponse.toString())
                append("file", message.file.toString())
                message.responseText?.let { append("responseText", it) }

            }

            val response = ktorClient.post(ApiUrls.SEND_MESSAGE_WITH_FILE) {
                setBody(MultiPartFormDataContent(formData))
            }

            if (response.status.isSuccess()) {
                emit(NetworkResponse.Success(data = message))
            } else {
                emit(NetworkResponse.Failure("Error: ${response.status}"))
            }

        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun receiveMessageByUserId(userId: String): Flow<NetworkResponse<List<MessageModel>>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val url = ApiUrls.MESSAGES_INBOX_BY_ID.replace("{userId}", userId)

            val response: HttpResponse = ktorClient.get(url)

            if (response.status == HttpStatusCode.OK) {
                val messages = response.body<List<MessageModel>>()
                emit(NetworkResponse.Success(messages))
            } else {
                emit(NetworkResponse.Failure("Error ${response.status.value}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun receiveMessageOutbox(): Flow<NetworkResponse<List<MessageModel>>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val url = ApiUrls.MESSAGES_OUTBOX

            val token = tokenManager.token.firstOrNull()

            if (token.isNullOrEmpty()) {
                emit(NetworkResponse.Failure("No auth token found"))
                return@flow
            }

            val response = ktorClient.get(url) {
                header("Authorization", "Bearer $token")
            }
            val bodyText = response.bodyAsText()
            println("DEBUG JSON: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                val messages = response.body<List<MessageModel>>()
                emit(NetworkResponse.Success(messages))
            } else {
                emit(NetworkResponse.Failure("Error en get outbox: ${response.status.value}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

    override fun updateMessage(message: MessageModel): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())

            val response = ktorClient.put(ApiUrls.UPDATE_MESSAGE) {
                contentType(ContentType.Application.Json)
                setBody(message)
            }

            if (response.status == HttpStatusCode.OK) {
                emit(NetworkResponse.Success(Unit))
            } else {
                emit(NetworkResponse.Failure("Error ${response.status.value}"))
            }

        } catch (e: Exception) {
            emit(NetworkResponse.Failure(error = e.toString()))
        }
    }

}
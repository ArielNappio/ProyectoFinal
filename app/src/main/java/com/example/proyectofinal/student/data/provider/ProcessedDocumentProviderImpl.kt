package com.example.proyectofinal.student.data.provider

import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.data.remoteData.model.Task
import com.example.proyectofinal.student.domain.provider.ProcessedDocumentProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProcessedDocumentProviderImpl(private val client: HttpClient) : ProcessedDocumentProvider {

    override fun getTaskById(id: Int): Flow<NetworkResponse<Task>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = client.get(ApiUrls.TASK)
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<Task>()
                emit(NetworkResponse.Success(responseBody))
            } else {
                emit(NetworkResponse.Failure("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun getAllTasks(): Flow<NetworkResponse<List<Task>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = client.get(ApiUrls.TASKS)
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<List<Task>>()
                emit(NetworkResponse.Success(responseBody))
            } else {
                emit(NetworkResponse.Failure("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun postTask(task: Task): Flow<NetworkResponse<Task>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response: HttpResponse = client.post(ApiUrls.TASK) {
                contentType(ContentType.Application.Json)
                setBody(task)
            }
            val responseBody = response.body<Task>()
            emit(NetworkResponse.Success(responseBody))
        } catch (e: ClientRequestException) {
            emit(NetworkResponse.Failure("Client error: ${e.response.status}"))
        } catch (e: ServerResponseException) {
            emit(NetworkResponse.Failure("Server error: ${e.response.status}"))
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.localizedMessage ?: "Unknown error"))
        }
    }
}
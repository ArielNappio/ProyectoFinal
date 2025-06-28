package com.example.proyectofinal.mail.data.repository

import android.util.Log
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.data.local.MessageDao
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.mapper.toDomain
import com.example.proyectofinal.mail.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MailRepoImpl(
    private val mailProvider: MailProvider,
    private val messageDao: MessageDao
) : MailRepository {

    override suspend fun sendMessage(message: MessageModel): NetworkResponse<MessageModel> {
        return try {
            when (val response = mailProvider.sendMessage(message).first()) {
                is NetworkResponse.Success -> {
                    messageDao.insertMessage(message.toEntity())
                    NetworkResponse.Success(response.data)
                }

                is NetworkResponse.Failure -> {
                    NetworkResponse.Failure(response.error)
                }

                is NetworkResponse.Loading -> {
                    NetworkResponse.Loading()
                }
            }
        } catch (e: Exception) {
            NetworkResponse.Failure(e.toString())
        }
    }

    override fun receiveMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>> = flow {
        emit(NetworkResponse.Loading())

        try {
            mailProvider.receiveMessageByUserId(userId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val messages = response.data ?: emptyList()
                        Log.d("Repo", "Mensajes recibidos desde API:\n$messages")

                        messages.forEach { message ->
                            messageDao.insertMessage(message.toEntity())
                        }

                        val localMessages = messageDao.getInboxMessages(userId).map { it.toDomain() }
                        emit(NetworkResponse.Success(localMessages))
                    }

                    is NetworkResponse.Failure -> {
                        val localMessages = messageDao.getInboxMessages(userId).map { it.toDomain() }
                        emit(
                            if (localMessages.isNotEmpty())
                                NetworkResponse.Success(localMessages)
                            else
                                NetworkResponse.Failure(response.error)
                        )
                    }

                    is NetworkResponse.Loading -> {}
                }
            }
        } catch (e: Exception) {
            val localMessages = messageDao.getInboxMessages(userId).map { it.toDomain() }
            emit(
                if (localMessages.isNotEmpty())
                    NetworkResponse.Success(localMessages)
                else
                    NetworkResponse.Failure(e.toString())
            )
        }
    }

    override fun receiveOutboxMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>> = flow {
        emit(NetworkResponse.Loading())

        try {
            mailProvider.receiveMessageOutbox().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val messages = response.data ?: emptyList()
                        messages.forEach { messageDao.insertMessage(it.toEntity()) }
                        emit(NetworkResponse.Success(messages))
                    }

                    is NetworkResponse.Failure -> {
                        val localMessages = messageDao.getOutboxMessages(userId).map { it.toDomain() }
                        if (localMessages.isNotEmpty()) {
                            Log.d("Repo", "Sin red, devolviendo outbox local: ${localMessages.size} mensajes")
                            emit(NetworkResponse.Success(localMessages))
                        } else {
                            emit(NetworkResponse.Failure(response.error))
                        }
                    }

                    is NetworkResponse.Loading -> {
                        emit(NetworkResponse.Loading())
                    }
                }
            }
        } catch (e: Exception) {
            val localMessages = messageDao.getOutboxMessages(userId).map { it.toDomain() }
            if (localMessages.isNotEmpty()) {
                Log.d("Repo", "Excepción, devolviendo outbox local: ${localMessages.size} mensajes")
                emit(NetworkResponse.Success(localMessages))
            } else {
                emit(NetworkResponse.Failure(e.toString()))
            }
        }
    }

    override suspend fun saveDraft(message: MessageModel) {
        withContext(Dispatchers.IO) {
            val draftEntity = message.toEntity()
            val existingDraft = messageDao.getDraftById(message.id)
            if (existingDraft != null) {
                messageDao.updateDraft(draftEntity)
            } else {
                messageDao.saveDraft(draftEntity)
            }
        }
    }

    override suspend fun getDrafts(): List<MessageModel> {
        return messageDao.getDrafts().map { it.toDomain() }
    }

    override suspend fun deleteDraftById(idMessage: Int) {
        messageDao.deleteDraftById(idMessage)
    }

    override suspend fun getDraftById(idMessage: Int): MessageModel {
        return messageDao.getDraftById(idMessage).toDomain()
    }
}

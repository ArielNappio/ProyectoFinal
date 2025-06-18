package com.example.proyectofinal.mail.data.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.data.local.MessageDao
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.mapper.toDomain
import com.example.proyectofinal.mail.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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

    override suspend fun receiveLastMessage(): NetworkResponse<MessageModel> {
        val entity = messageDao.getLastMessage()
        return if (entity != null) {
            NetworkResponse.Success(entity.toDomain())
        } else {
            NetworkResponse.Failure("No se encontró ningún mensaje")
        }
    }

    override suspend fun getInboxMessages(currentUserId: String): List<MessageModel> {
        return messageDao.getInboxMessages(
            currentUserId
        ).map { it.toDomain() }
    }

    override suspend fun getOutboxMessages(currentUserId: String): List<MessageModel> {
        return messageDao.getOutboxMessages(
            currentUserId
        ).map { it.toDomain() }
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

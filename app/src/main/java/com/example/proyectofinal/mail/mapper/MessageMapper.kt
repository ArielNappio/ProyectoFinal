package com.example.proyectofinal.mail.mapper

import com.example.proyectofinal.mail.data.entity.MessageEntity
import com.example.proyectofinal.mail.domain.model.MessageModel

fun MessageEntity.toDomain(): MessageModel {
    return MessageModel(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        filePath = filePath
        // faltaria el userFromId, userToID ?
    )
}

fun MessageModel.toEntity(
    userFromId: Int = 0,
    userToID: Int = 0,
    isDraft: Boolean = true,
    timestamp: Long = System.currentTimeMillis()
): MessageEntity {
    return MessageEntity(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        filePath = filePath,
        userFromId = userFromId.toString(),
        userToID = userToID.toString(),
        isDraft = isDraft,
        timestamp = timestamp
    )
}

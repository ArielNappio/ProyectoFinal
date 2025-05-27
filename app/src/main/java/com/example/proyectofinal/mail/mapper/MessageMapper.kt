package com.example.proyectofinal.mail.mapper

import com.example.proyectofinal.mail.data.entity.MessageEntity
import com.example.proyectofinal.mail.domain.model.MessageModel

fun MessageEntity.toDomain(): MessageModel {
    return MessageModel(
        sender = sender,
        subject = subject,
        date = date,
        content = content
        // faltaria el id, userFromId, userToID ?
    )
}

fun MessageModel.toEntity(
    id: Int = 0,
    userFromId: Int = 0,
    userToID: Int = 0,
    isDraft: Boolean = false,
    timestamp: Long = System.currentTimeMillis()
): MessageEntity {
    return MessageEntity(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        userFromId = userFromId.toString(),
        userToID = userToID.toString(),
        isDraft = isDraft,
        timestamp = timestamp
    )
}

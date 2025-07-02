package com.example.proyectofinal.mail.mapper

import com.example.proyectofinal.mail.data.entity.MessageEntity
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.MessageModelDto

fun MessageEntity.toDomain(): MessageModel {
    return MessageModel(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        file = filePath,
        isDraft = isDraft,
        isResponse = isResponse,
        userToId = this@toDomain.userToId,
        userFromId = userFromId,
        responseText = responseText
    )
}

fun MessageModel.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        filePath = file,
        userFromId = userFromId,
        isDraft = isDraft,
        isResponse = isResponse,
        responseText = responseText,
        userToId = userToId,
    )
}

fun MessageModelDto.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        filePath = file,
        userFromId = userFromId,
        isDraft = isDraft,
        isResponse = isResponse,
        responseText = responseText,
        userToId = userToId,
        isRead = isRead?: false
    )
}

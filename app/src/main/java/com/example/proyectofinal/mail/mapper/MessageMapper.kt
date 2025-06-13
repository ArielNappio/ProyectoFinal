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
        formPath = filePath,
        isDraft = isDraft,
        isResponse = isResponse,
        studentId = userToId,
        userFromId = userFromId,
        responseText = responseText
    )
}

fun MessageModel.toEntity(): MessageEntity {
    return MessageEntity(
        sender = sender,
        subject = subject,
        date = date,
        content = content,
        filePath = formPath,
        userFromId = userFromId,
        isDraft = isDraft,
        isResponse = isResponse,
        responseText = responseText,
        userToId = studentId,
    )
}

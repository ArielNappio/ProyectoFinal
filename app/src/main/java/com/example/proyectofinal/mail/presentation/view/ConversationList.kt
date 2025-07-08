package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.presentation.component.MessageThreadItem

@Composable
fun ConversationList(conversation: List<MessageModelDto>) {
    Column {
        conversation.forEach { message ->
            MessageThreadItem(
                sender = message.sender,
                recipient = message.userToId,
                date = message.date,
                content = message.content,
                file = message.file
            )
        }
    }
}

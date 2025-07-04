package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.presentation.component.ConversationItem

@Composable
fun ConversationList(conversation: List<MessageModelDto>) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        conversation.forEachIndexed { index, message ->
            ConversationItem(message)
            if (index != conversation.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )
            }
        }
    }
}

package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.OutboxMessageModel
import com.example.proyectofinal.mail.presentation.component.MessageItem
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun InboxScreen(
    modifier: Modifier,
    navController: NavController,
    mailboxType: MailboxType,
    onMessageClick: (MessageModel) -> Unit
) {
    val viewModel = koinViewModel<InboxViewModel>()
    val messagesState = when (mailboxType) {
        MailboxType.INBOX -> viewModel.inboxMessages
        MailboxType.OUTBOX -> viewModel.outboxMessages
        MailboxType.DRAFT -> viewModel.draftMessages
    }
    val messages by messagesState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = when (mailboxType) {
                MailboxType.INBOX -> "Bandeja de Entrada"
                MailboxType.OUTBOX -> "Bandeja de Salida"
                MailboxType.DRAFT -> "Borradores"
            },
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Botón de Redactar
        Button(
            onClick = { navController.navigate(ScreensRoute.Message.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomGreen,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Redactar", tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Redactar", color = Color.White, fontSize = 18.sp)
        }

        // Lista de mensajes
        MessageList(
            messages = messages,
            type = mailboxType,
            onMessageClick = onMessageClick,
            onReply = if (mailboxType == MailboxType.INBOX) {
                { message ->
                    navController.navigate("reply/${message.id}")
                }
            } else null,
            onMarkStatus = if (mailboxType == MailboxType.OUTBOX) {
                { message, status ->
                    viewModel.updateMessageStatus(
                        message.id,
                        OutboxMessageModel.MessageStatus.valueOf(status)
                    )
                }
            } else null,
            onDelete = if (mailboxType == MailboxType.DRAFT) {
                { id ->
                    viewModel.discardDraft(id.toInt())
                }
            } else null,
            onContinueEditing = if (mailboxType == MailboxType.DRAFT) {
                { message ->
                    navController.navigate("${ScreensRoute.Message.route}?draftId=${message.id}")
                }
            } else null
        )
    }
}

@Composable
fun MessageList(
    messages: List<MessageModel>,
    type: MailboxType,
    onMessageClick: (MessageModel) -> Unit,
    onReply: ((MessageModel) -> Unit)? = null,
    onMarkStatus: ((MessageModel, String) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null,
    onContinueEditing: ((MessageModel) -> Unit)? = null
) {
    if (messages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay mensajes aún.",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { message ->
                MessageItem(
                    message = message,
                    type = type,
                    onClick = { onMessageClick(message) },
                    onReply = onReply,
                    onMarkStatus = onMarkStatus,
                    onDelete = onDelete,
                    onContinueEditing = onContinueEditing
                )
            }
        }
    }
}


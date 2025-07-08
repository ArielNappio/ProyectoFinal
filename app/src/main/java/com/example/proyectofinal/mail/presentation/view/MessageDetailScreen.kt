package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.theme.BlueDark
import com.example.proyectofinal.core.theme.GreenLight
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.presentation.component.MessageThreadItem
import com.example.proyectofinal.mail.presentation.viewmodel.ConversationViewModel
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    message: MessageModel,
    mailboxType: MailboxType,
    userEmail: String,
    recipientEmail: String,
    onBack: () -> Unit,
    onReply: ((MessageModel) -> Unit)? = null
) {
    val conversationVm: ConversationViewModel = koinViewModel()
    val conversationState by conversationVm.conversationState.collectAsState()

    LaunchedEffect(message.id) {
        conversationVm.loadConversation(message.id.toString())
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { AppText("Detalle de mensaje", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 👉 Mensaje principal con mismo estilo que las conversaciones
            MessageThreadItem(
                sender = when (mailboxType) {
                    MailboxType.INBOX -> recipientEmail
                    MailboxType.OUTBOX, MailboxType.DRAFT -> userEmail
                },
                recipient = when (mailboxType) {
                    MailboxType.INBOX -> userEmail
                    MailboxType.OUTBOX, MailboxType.DRAFT -> message.sender
                },
                date = message.date,
                content = message.content,
                file = message.file
            )

            // 👉 Respuesta asociada si tiene
            if (message.isResponse) {
                AppText(
                    "Respuesta:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                AppText(
                    message.responseText ?: "Sin respuesta",
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // 👉 Conversación
            when (conversationState) {
                is NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success -> {
                    val conversation = (conversationState as NetworkResponse.Success<List<MessageModelDto>>).data ?: emptyList()
                    if (conversation.isNotEmpty()) {
                        ConversationList(conversation)
                    }
                }
                is NetworkResponse.Failure -> {
                    Text("No se pudo cargar la conversación", color = Color.Red)
                }
            }

            Spacer(Modifier.weight(1f))

            // 👉 Botón responder si aplica
            if (mailboxType == MailboxType.INBOX && onReply != null) {
                Button(
                    onClick = { onReply(message) },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Reply,
                        contentDescription = "Responder",
                        tint = GreenLight
                    )
                    Spacer(Modifier.width(8.dp))
                    AppText("Responder", color = GreenLight)
                }
            }
        }
    }
}



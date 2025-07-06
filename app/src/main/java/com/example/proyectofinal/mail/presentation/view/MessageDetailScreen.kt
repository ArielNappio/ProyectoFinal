package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.presentation.theme.BlueDark
import com.example.proyectofinal.core.presentation.theme.GreenLight
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.presentation.viewmodel.ConversationViewModel
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel
import java.io.File

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
        ) {
            AppText("De:", color = Color.White, fontWeight = FontWeight.Bold)
            AppText(
                text = when (mailboxType) {
                    MailboxType.INBOX -> recipientEmail // remitente (desde userFromId)
                    MailboxType.OUTBOX, MailboxType.DRAFT -> userEmail
                }
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.Gray)

            AppText("Para:", color = Color.White, fontWeight = FontWeight.Bold)
            AppText(
                text = when (mailboxType) {
                    MailboxType.INBOX -> userEmail
                    MailboxType.OUTBOX, MailboxType.DRAFT -> message.sender
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)

            AppText("Asunto:", color = Color.White, fontWeight = FontWeight.Bold)
            AppText(message.subject, color = Color.White)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

            AppText("Fecha:", color = Color.White, fontWeight = FontWeight.Bold)
            AppText(message.date, color = Color.White)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

            AppText("Mensaje:", color = Color.White, fontWeight = FontWeight.Bold)
            AppText(
                message.content,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            message.file?.let {
                if (it.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = GreenLight)
                        Spacer(Modifier.width(8.dp))
                        AppText(
                            File(it).name,
                            color = GreenLight
                        )
                    }
                }
            }
            when (conversationState) {
                is NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success -> {
                    ConversationList(
                        conversation = (conversationState as NetworkResponse.Success<List<MessageModelDto>>).data ?: emptyList()
                    )
                }
                is NetworkResponse.Failure -> {
                    Text("No se pudo cargar la conversación", color = Color.Red)
                }
            }

            if (message.isResponse) {
                Spacer(Modifier.height(16.dp))
                AppText("Respuesta:", color = Color.White, fontWeight = FontWeight.Bold)
                AppText(
                    message.responseText ?: "Sin respuesta",
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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


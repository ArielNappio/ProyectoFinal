package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.core.theme.BlueDark
import com.example.proyectofinal.core.theme.GreenLight
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
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
    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Detalle de mensaje", color = Color.White) },
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
            Text("De:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = when (mailboxType) {
                    MailboxType.INBOX -> message.sender
                    MailboxType.OUTBOX, MailboxType.DRAFT -> userEmail
                },
                color = Color.White,
                fontSize = 16.sp
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.Gray)

            Text("Para:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = when (mailboxType) {
                    MailboxType.INBOX -> userEmail
                    MailboxType.OUTBOX, MailboxType.DRAFT -> recipientEmail
                },
                color = Color.White,
                fontSize = 16.sp
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)

            Text("Asunto:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(message.subject, color = Color.White, fontSize = 16.sp)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

            Text("Fecha:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(message.date, color = Color.White, fontSize = 16.sp)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))

            Text("Mensaje:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                message.content,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            message.file?.let {
                if (it.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = GreenLight)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            File(it).name,
                            color = GreenLight,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (message.isResponse) {
                Spacer(Modifier.height(16.dp))
                Text("Respuesta:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    message.responseText ?: "Sin respuesta",
                    color = Color.White,
                    fontSize = 16.sp,
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
                    Text("Responder", color = GreenLight, fontSize = 18.sp)
                }
            }
        }
    }
}


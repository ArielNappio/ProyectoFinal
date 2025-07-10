package com.example.proyectofinal.mail.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.mail.domain.model.MailboxType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageItem(
    message: MessageModel,
    type: MailboxType,
    onClick: () -> Unit,
    onReply: ((MessageModel) -> Unit)? = null,
    onMarkStatus: ((MessageModel, String) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null,
    onContinueEditing: ((MessageModel) -> Unit)? = null
) {
    val messageVm: MessageViewModel = koinViewModel()

    val emailState = messageVm.to.collectAsState()

    LaunchedEffect(message.userFromId) {
        messageVm.getEmailByUserId(message.userFromId)
    }

    val fromEmail = emailState.value.ifBlank { "Cargando..." }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            headlineContent = {
                when(type) {
                    MailboxType.INBOX -> {
                        AppText(
                            text = "De: $fromEmail",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            isTitle = true,
                        )
                    }
                    MailboxType.OUTBOX -> {
                        AppText(
                            text = "Para: ${message.sender}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            isTitle = true,
                        )
                }

                    MailboxType.DRAFT -> {
                        AppText(
                            text = "Para: ${message.sender}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            isTitle = true,
                        )
                    }
                }

            },
            supportingContent = {
                Column {
                    AppText(
                        text = "Asunto: ${message.subject}",
                        color = Color.White,
                    )
                    AppText(
                        text = "Fecha: ${message.date}",
                        color = Color.Gray,
                    )
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (type) {
                        MailboxType.INBOX -> {
                            IconButton(
                                onClick = { onReply?.invoke(message) },
                                modifier = Modifier.semantics {
                                    contentDescription = "Responder al mensaje"
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Reply,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }

                        MailboxType.OUTBOX -> {
//                            listOf("Leído", "No leído", "Entregado").forEach { status ->
//                                IconButton(
//                                    onClick = { onMarkStatus?.invoke(message, status) },
//                                    modifier = Modifier.semantics {
//                                        contentDescription = "Marcar como $status"
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = when (status) {
//                                            "Leído" -> Icons.Default.Visibility
//                                            "No leído" -> Icons.Default.VisibilityOff
//                                            "Entregado" -> Icons.Default.Done
//                                            else -> Icons.Default.Info
//                                        },
//                                        contentDescription = null,
//                                        tint = Color.White
//                                    )
//                                }
//                            }
                        }

                        MailboxType.DRAFT -> {
                            IconButton(
                                onClick = { onContinueEditing?.invoke(message) },
                                modifier = Modifier
                                    .size(64.dp)
                                    .semantics { contentDescription = "Editar borrador" }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDelete?.invoke(message.id.toString()) },
                                modifier = Modifier
                                    .size(64.dp)
                                    .semantics { contentDescription = "Eliminar borrador" }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        )

        HorizontalDivider(color = Color.DarkGray)
    }
}


package com.example.proyectofinal.mail.presentation.component

import com.example.proyectofinal.mail.domain.model.MailboxType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.proyectofinal.core.theme.BlackGray
import com.example.proyectofinal.mail.domain.model.MessageModel

@Composable
fun MessageItem(
    message: MessageModel,
    type: MailboxType,
    onClick: () -> Unit,
    onReply: ((MessageModel) -> Unit)? = null,
    onMarkStatus: ((MessageModel, String) -> Unit)? = null, // status: "Leído", "No leído", "Entregado"
    onDelete: ((String) -> Unit)? = null,
    onContinueEditing: ((MessageModel) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackGray)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenido principal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "De: ${message.sender}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Asunto: ${message.subject}",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Fecha: ${message.date}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Acciones según tipo
            Row {
                when (type) {
                    MailboxType.INBOX -> {
                        IconButton(
                            onClick = { onReply?.invoke(message) },
                            modifier = Modifier.semantics {
                                contentDescription = "Responder al mensaje"
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = Color.White)
                        }
                    }

                    MailboxType.OUTBOX -> {
                        listOf("Leído", "No leído", "Entregado").forEach { status ->
                            IconButton(
                                onClick = { onMarkStatus?.invoke(message, status) },
                                modifier = Modifier.semantics {
                                    contentDescription = "Marcar como $status"
                                }
                            ) {
                                Icon(
                                    imageVector = when (status) {
                                        "Leído" -> Icons.Default.Visibility
                                        "No leído" -> Icons.Default.VisibilityOff
                                        "Entregado" -> Icons.Default.Done
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    MailboxType.DRAFT -> {
                        IconButton(
                            onClick = { onContinueEditing?.invoke(message) },
                            modifier = Modifier.semantics {
                                contentDescription = "Continuar escribiendo"
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        }

                        IconButton(
                            onClick = { onDelete?.invoke(message.id.toString()) },
                            modifier = Modifier.semantics {
                                contentDescription = "Eliminar borrador"
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
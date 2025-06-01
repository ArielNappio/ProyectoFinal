package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.OutboxMessageModel
import com.example.proyectofinal.mail.presentation.component.MessageItem
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    navController: NavController,
    mailboxType: MailboxType,
    onMessageClick: (MessageModel) -> Unit
) {
    val viewModel = koinViewModel<InboxViewModel>()

    val messagesState = when(mailboxType) {
        MailboxType.INBOX -> viewModel.inboxMessages
        MailboxType.OUTBOX -> viewModel.outboxMessages
        MailboxType.DRAFT -> viewModel.draftMessages
    }

    val messages by messagesState.collectAsState()

    val menuExpanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(
                                id = if (LocalTheme.current.isDark) R.drawable.wirin_logo_dark else R.drawable.wirin_logo_light
                            ),
                            contentDescription = "Volver al menú anterior",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (mailboxType) {
                            MailboxType.INBOX -> "Bandeja de Entrada"
                            MailboxType.OUTBOX -> "Bandeja de Salida"
                            MailboxType.DRAFT -> "Borradores"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Nuevo Mensaje",
                                    fontSize = 18.sp
                                )
                            },
                            onClick = { navController.navigate(ScreensRoute.Message.route) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Nuevo mensaje",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Bandeja de Entrada",
                                    fontSize = 18.sp
                                )
                            },
                            onClick = { navController.navigate("mail/inbox") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Inbox,
                                    contentDescription = "Bandeja de Entrada",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Bandeja de Salida",
                                    fontSize = 18.sp
                                )
                            },
                            onClick = { navController.navigate("mail/outbox") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Bandeja de Salida",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Borradores",
                                    fontSize = 18.sp
                                )
                            },
                            onClick = { navController.navigate("mail/drafts") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Drafts,
                                    contentDescription = "Borradores",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                    }
                }
            }
        )
        Button(
            onClick = { navController.navigate(ScreensRoute.Message.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomGreen,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Redactar",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Redactar",
                color = Color.White,
                fontSize = 18.sp
            )
        }


        MessageList(
            messages = messages,
            type = mailboxType,
            onMessageClick = onMessageClick,
            onReply = if (mailboxType == MailboxType.INBOX) { { message ->
                navController.navigate("reply/${message.id}")
            } } else null,
            onMarkStatus = if (mailboxType == MailboxType.OUTBOX) { { message, status ->
                viewModel.updateMessageStatus(message.id, OutboxMessageModel.MessageStatus.valueOf(status))
            } } else null,
            onDelete = if (mailboxType == MailboxType.DRAFT) { { id ->
                viewModel.discardDraft(id.toInt())
            } } else null,
            onContinueEditing = if (mailboxType == MailboxType.DRAFT) { { message ->
                navController.navigate("${ScreensRoute.Message.route}?draftId=${message.id}")
            } } else null
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


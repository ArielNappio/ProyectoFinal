package com.example.proyectofinal.mail.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.OutboxMessageModel
import com.example.proyectofinal.mail.presentation.component.MessageItem
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun InboxScreen(
    navController: NavController,
    mailboxType: MailboxType,
    onMessageClick: (MessageModel) -> Unit
) {
    val viewModel = koinViewModel<InboxViewModel>()
    val receivedState by viewModel.receivedMessages.collectAsState()

    val messages = when (mailboxType) {
        MailboxType.INBOX -> {
            when (receivedState) {
                is NetworkResponse.Success -> (receivedState as NetworkResponse.Success<List<MessageModel>>).data ?: emptyList()
                else -> emptyList()
            }
        }
        MailboxType.OUTBOX -> viewModel.outboxMessages.collectAsState().value
        MailboxType.DRAFT -> viewModel.draftMessages.collectAsState().value
    }

    Column(modifier = Modifier.fillMaxSize()) {
        InboxScreenTopBar(
            navController = navController,
            mailboxType = mailboxType
        )

        // Botón de Redactar
        Button(
            onClick = { navController.navigate(ScreensRoute.Message.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CustomGreen, contentColor = Color.White)
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
fun InboxScreenTopBar(
    navController: NavController,
    mailboxType: MailboxType
) {
    val menuExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp)
    ) {
        // Logo centrado
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.wirin_25
                ),
                contentDescription = "Logo de Wirin",
                modifier = Modifier.size(48.dp),
                colorFilter = if (LocalTheme.current.isDark) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Segunda fila con back, título y menú
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { navController.navigate(ScreensRoute.Home.route) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Volver",
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate(ScreensRoute.Home.route)
                    }
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Box {
                IconButton(onClick = { menuExpanded.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        modifier = Modifier.size(26.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded.value,
                    onDismissRequest = { menuExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nuevo Mensaje", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate(ScreensRoute.Message.route)
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Add, contentDescription = "Nuevo mensaje", modifier = Modifier.size(24.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bandeja de Entrada", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/inbox")
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Inbox, contentDescription = "Bandeja de Entrada", modifier = Modifier.size(24.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bandeja de Salida", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/outbox")
                        },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Bandeja de Salida", modifier = Modifier.size(24.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Borradores", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/drafts")
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Drafts, contentDescription = "Borradores", modifier = Modifier.size(24.dp))
                        }
                    )
                }
            }
        }
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


package com.example.proyectofinal.mail.presentation.view

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.proyectofinal.core.presentation.theme.LocalTheme
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.component.MessageItem
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxScreen(
    navController: NavController,
    mailboxType: MailboxType,
    onMessageClick: (MessageModel) -> Unit
) {
    val viewModel = koinViewModel<InboxViewModel>()

    val receivedInboxState by viewModel.receivedInboxMessages.collectAsState()
    val receivedOutboxState by viewModel.receivedOutboxMessages.collectAsState()
    val inboxMessages by viewModel.inboxMessages.collectAsState()
    val outboxMessages by viewModel.outboxMessages.collectAsState()
    val draftMessages by viewModel.draftMessages.collectAsState()

    val messages = when (mailboxType) {
        MailboxType.INBOX -> inboxMessages
        MailboxType.OUTBOX -> outboxMessages
        MailboxType.DRAFT -> draftMessages
    }

    Column(modifier = Modifier.fillMaxSize()) {
        InboxScreenTopBar(navController, mailboxType)

        Button(
            onClick = { navController.navigate("${ScreensRoute.Message.route}?draftId=-1&replyToSubject=&fromUserId=") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff2e7d32),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Redactar", tint = Color.White)
            Spacer(Modifier.width(6.dp))
            AppText("Redactar", color = Color.White)
        }

        if (mailboxType == MailboxType.DRAFT) {
            MessageList(
                messages = messages,
                type = mailboxType,
                onMessageClick = onMessageClick,
                onReply = null,
                onMarkStatus = null,
                onDelete = { id -> viewModel.discardDraft(id.toInt()) },
                onContinueEditing = { message ->
                    navController.navigate("${ScreensRoute.Message.route}?draftId=${message.id}")
                }
            )
        } else {
            when (receivedInboxState) {
                is NetworkResponse.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is NetworkResponse.Failure -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText(
                            text = "Error al cargar mensajes",
                            color = Color.Red)
                    }
                }

                is NetworkResponse.Success -> {
                    MessageList(
                        messages = messages,
                        type = mailboxType,
                        onMessageClick = onMessageClick,
                        onReply = if (mailboxType == MailboxType.INBOX) {
                            { message ->
                                navController.navigate(
                                    "${ScreensRoute.Message.route}?draftId=-1&replyToSubject=${
                                        Uri.encode(message.subject)
                                    }&fromUserId=${message.userFromId}"
                                )
                            }
                        } else null,
                        onMarkStatus = if (mailboxType == MailboxType.OUTBOX) {
                            { message, status ->
                                // Si lo necesitás
                            }
                        } else null,
                        onDelete = null,
                        onContinueEditing = null
                    )
                }
            }
        }
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
                colorFilter = if (LocalTheme.current.isDark) ColorFilter.tint(Color.White) else ColorFilter.tint(
                    Color.Black
                )
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

            AppText(
                text = when (mailboxType) {
                    MailboxType.INBOX -> "Recibidos"
                    MailboxType.OUTBOX -> "Enviados"
                    MailboxType.DRAFT -> "Borradores"
                },
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
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Nuevo mensaje",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Recibidos", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/inbox")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Inbox,
                                contentDescription = "Recibidos",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Enviados", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/outbox")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviados",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Borradores", fontSize = 18.sp) },
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate("mail/drafts")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Drafts,
                                contentDescription = "Borradores",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
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
            AppText(
                text = "No hay mensajes aún.",
                color = Color.White,
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


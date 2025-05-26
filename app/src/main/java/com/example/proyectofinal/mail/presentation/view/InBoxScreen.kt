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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.mail.domain.MailboxType
import com.example.proyectofinal.mail.domain.MessageModel
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
    val messages by when (mailboxType) {
        MailboxType.INBOX -> viewModel.inboxMessages.collectAsState()
        MailboxType.OUTBOX -> viewModel.outboxMessages.collectAsState()
    }
    val menuExpanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = if (LocalTheme.current.isDark) R.drawable.wirin_logo_dark else R.drawable.wirin_logo_light
                        ),
                        contentDescription = "Logo de Wirin",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (mailboxType) {
                            MailboxType.INBOX -> "Bandeja de Entrada"
                            MailboxType.OUTBOX -> "Bandeja de Salida"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nuevo Mensaje") },
                            onClick = { navController.navigate(ScreensRoute.Message.route) })
                        DropdownMenuItem(
                            text = { Text("Bandeja de Entrada") },
                            onClick = {
                                navController.navigate("mail/inbox")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bandeja de Salida") },
                            onClick = {
                                navController.navigate("mail/outbox")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Borradores") },
                            onClick = { })
                    }
                }
            }
        )

        Button(
            onClick = { navController.navigate(ScreensRoute.Message.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Create, contentDescription = "Redactar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Redactar")
        }

        MessageList(messages, onMessageClick)
    }
}



@Composable
private fun MessageList(
    messages: List<MessageModel>,
    onMessageClick: (MessageModel) -> Unit
) {
    if (messages.isEmpty()) {
        // Si no hay mensajes, mostramos un texto amigable
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
                MessageItem(message, onClick = { onMessageClick(message) })
            }
        }
    }
}

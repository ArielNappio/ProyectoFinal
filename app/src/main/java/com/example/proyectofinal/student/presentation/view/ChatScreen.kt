package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.student.data.model.ChatMessage

@Composable
fun ChatScreen(modifier: Modifier = Modifier, navController: NavController) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! ¿En qué puedo ayudarte?", isUser = false),
            ChatMessage("Tengo dudas sobre una tarea.", isUser = true),
            ChatMessage("Claro, contame un poco más.", isUser = false)
        )
    }

    var currentMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll al final cuando se agrega un mensaje
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Chat con Wirin",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            itemsIndexed(messages) { _, message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                // Acá iría la lógica para grabar voz o iniciar reconocimiento
                // TODO: manejar permisos y grabación
            }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Micrófono"
                )
            }

            TextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Escribí un mensaje...") },
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            IconButton(onClick = {
                if (currentMessage.isNotBlank()) {
                    messages.add(ChatMessage(currentMessage.trim(), isUser = true))
                    messages.add(ChatMessage("Gracias por tu mensaje, lo reviso ahora.", isUser = false))
                    currentMessage = ""
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFE0E0E0)
    val bubbleShape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = bubbleShape)
                .padding(12.dp)
        ) {
            Text(text = message.text, fontSize = 16.sp, color = Color.Black)
        }
    }
}
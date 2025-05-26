package com.example.proyectofinal.mail.presentation.component

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectofinal.core.theme.BlackGray
import com.example.proyectofinal.mail.domain.model.MessageModel

@Composable
fun MessageItem(message: MessageModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageItem() {
    MessageItem(
        message = MessageModel(
            "Rocio Mercado",
            "URGENTE",
            "hoy",
            "esto es el contenido del correo"
        ),
        onClick = TODO()
    )
}
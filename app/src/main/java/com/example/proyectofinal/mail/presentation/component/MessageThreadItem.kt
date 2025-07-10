package com.example.proyectofinal.mail.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.student.util.formatHumanReadableDate
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import java.io.File

@Composable
fun MessageThreadItem(
    sender: String,
    recipient: String,
    date: String,
    content: String,
    file: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = Color(0xFF1E1E1E),
//        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppText("De: $sender", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                AppText(formatHumanReadableDate(date), color = Color.Gray)
            }
            AppText("Para: $recipient", color = Color.White, modifier = Modifier.padding(top = 4.dp))
            AppText(content, color = Color.White, modifier = Modifier.padding(top = 8.dp))

            file?.takeIf { it.isNotBlank() }?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Adjunto", tint = Color.Green)
                    Spacer(Modifier.width(8.dp))
                    AppText(File(it).name, color = Color.Green)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageThreadItem() {
    MessageThreadItem(
        sender = "soporte@mail.com",
        recipient = "usuario@mail.com",
        date = "9 jul 2025",
        content = "Hola, este es un mensaje de prueba",
        file = null
    )
}

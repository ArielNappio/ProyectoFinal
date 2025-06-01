package com.example.proyectofinal.mail.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.core.theme.BlackGray
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormScreen(
    viewModel: MessageViewModel,
    onDismiss: () -> Unit,
    onMessageSent: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var career by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Solicitar Apunte",
                color = Color.White
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                TextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor") },
                    singleLine = true
                )
                TextField(
                    value = career,
                    onValueChange = { career = it },
                    label = { Text("Carrera") },
                    singleLine = true
                )
                TextField(
                    value = signature,
                    onValueChange = { signature = it },
                    label = { Text("Firma") },
                    singleLine = true
                )
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val file = viewModel.saveFormToFile(context, title, author, career, signature, date)
                    val message = MessageModel(
                        id = 0,
                        sender = "Usuario",
                        subject = "Formulario Adjunto",
                        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        content = "Se adjunta un formulario.",
                        filePath = file.absolutePath
                    )
                    viewModel.sendMessage(message)
                    onMessageSent()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
            ) {
                Text("Enviar", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancelar")
            }
        },
        containerColor = BlackGray
    )
}

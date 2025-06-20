package com.example.proyectofinal.mail.presentation.component

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.core.theme.BlackGray
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormScreen(
    viewModel: MessageViewModel,
    onDismiss: () -> Unit,
    onMessageSent: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var career by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var chapters by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

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
                FormTextField("Carrera", "Ej: Abogacía", career) { career = it }
                FormTextField("Comisión", "Ej: 3966", subject) { subject = it }
                FormTextField("Apunte", "Ej: Clase 1- ¿Qué es una ley?", note) { note = it }
                FormTextField("Unidades", "Ej: 1 a 5", chapters) { chapters = it }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate.ifEmpty { "Fecha de entrega" },
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.White
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (viewModel.isFormValid(career, subject, note, chapters, selectedDate)){
                        val file = viewModel.saveFormToFile(
                            context,
                            career,
                            subject,
                            note,
                            chapters,
                            selectedDate
                        )
                        Log.d("FormScreen", "Archivo guardado en: ${file.absolutePath}")

                        viewModel.updateFormPath(file.absolutePath)

                        val message = MessageModel(
                            id = 0,
                            sender = "Usuario",
                            subject = "Formulario Adjunto",
                            date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toString(),
                            content = "Se adjunta un formulario.",
                            formPath = file.absolutePath,
                            isDraft = false,
                            isResponse = false,
                            studentId = "",
                            userFromId = ""
                        )
                        viewModel.sendMessage()
                        onMessageSent()
                    }
                    else {
                        Toast.makeText(context, "Por favor completá todos los campos.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
            ) {
                Text("Guardar", color = Color.White)
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

@Composable
fun FormTextField(
    label: String,
    placeholderText: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholderText,
                    color = Color.Gray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = CustomGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}



package com.example.proyectofinal.mail.presentation.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.theme.CustomGreen2
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    onSendComplete: () -> Unit,
    onCancel: () -> Unit,
    onDraftSaved: () -> Unit
) {
    val viewModel: MessageViewModel = koinViewModel()
    val to by viewModel.to.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val message by viewModel.message.collectAsState()
    val sendState by viewModel.sendMessageState.collectAsState()
    val draftSavedEvent = viewModel.draftSavedEvent
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(draftSavedEvent) {
        draftSavedEvent.collect {
            Toast.makeText(context, "Borrador guardado", Toast.LENGTH_SHORT).show()
            onDraftSaved()
        }
    }
    LaunchedEffect(sendState) {
        if (sendState is NetworkResponse.Success) {
            onSendComplete()
        }
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Mensaje", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelar redacción",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val newMessage = MessageModel(
                                sender = to,
                                subject = subject,
                                content = message,
                                date = LocalDateTime.now().toString(),
                            )
                            viewModel.sendMessage(newMessage)
                        },
                        enabled = to.isNotBlank() && message.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomGreen2)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar mensaje",
                            tint = Color.White
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Enviar", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = to,
                onValueChange = { viewModel.updateTo(it) },
                label = { Text("Para") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = subject,
                onValueChange = { viewModel.updateSubject(it) },
                label = { Text("Asunto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar")
                Spacer(Modifier.width(4.dp))
                Text("Adjuntar")
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { viewModel.updateMessage(it) },
                    label = { Text("Mensaje") },
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Grabar audio")
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Descartar", color = Color.White)
                }

                Button(
                    onClick = { viewModel.saveDraft() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Guardar como borrador")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveDraft()
                        showDialog = false
                        onCancel()
                    }
                ) {
                    Text("Guardar borrador")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onCancel()
                    }
                ) {
                    Text("Salir sin guardar")
                }
            },
            title = { Text("¿Salir de la redacción?") },
            text = { Text("¿Querés guardar este mensaje como borrador antes de salir?") },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

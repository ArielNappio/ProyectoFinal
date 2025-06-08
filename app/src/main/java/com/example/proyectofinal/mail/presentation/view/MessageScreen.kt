package com.example.proyectofinal.mail.presentation.view

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.audio.speechrecognizer.SpeechRecognizerManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.theme.BlueDark
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.core.theme.CustomRed
import com.example.proyectofinal.core.theme.GreenLight
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.presentation.component.FormScreen
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    draftId: Int,
    onSendComplete: () -> Unit,
    onCancel: () -> Unit,
    onDraftSaved: () -> Unit
) {
    val viewModel: MessageViewModel = koinViewModel()

    val to by viewModel.to.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val message by viewModel.message.collectAsState()
    val formPath by viewModel.formPath.collectAsState()
    val attachments by viewModel.attachments.collectAsState()
    val sendState by viewModel.sendMessageState.collectAsState()
    val draftSavedEvent = viewModel.draftSavedEvent
    val context = LocalContext.current

    var isDialogOpen by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addAttachment(it.toString())
        }
    }

    // Speech recognizer setup
    val voiceToText by viewModel.voiceToText.collectAsState()

    val speechRecognizerManager = remember {
        SpeechRecognizerManager(
            context = context,
            onResult = { result ->
                viewModel.appendToMessage(result)
            },
            onError = { error ->
                Log.e("SpeechRecognizer", error)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerManager.stopListening()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.draftErrorEvent.collect { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.messageErrorEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(draftId) {
        if (draftId != -1) {
            viewModel.loadDraft(draftId)
        }
    }

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
                            Icons.Default.Close,
                            contentDescription = "Salir",
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
                                formPath = formPath,
                                attachments = attachments,
                                date = Date.from(
                                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                                ),
                                id = 0,
                                isDraft = false,
                                isResponse = false,
                                studentId = "",
                                userFromId = ""
                            )
                            viewModel.sendMessage(newMessage)
                            Log.d("MessageScreen", "Message sent: $newMessage")
                        },
                        enabled = to.isNotBlank() && message.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
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
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Campo "Para"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Para:", modifier = Modifier.width(60.dp), color = Color.White)
                TextField(
                    value = to,
                    onValueChange = { viewModel.updateTo(it) },
                    placeholder = { Text("Destinatario", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.LightGray
                    )
                )
            }
            HorizontalDivider(color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            // Campo "Asunto"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Asunto:", modifier = Modifier.width(60.dp), color = Color.White)
                TextField(
                    value = subject,
                    onValueChange = { viewModel.updateSubject(it) },
                    placeholder = { Text("Asunto", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.LightGray
                    )
                )
            }
            HorizontalDivider(color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            // Acciones encima del cuerpo del mensaje
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = "Adjuntar archivo",
                        tint = GreenLight
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Adjuntar", color = GreenLight)
                }

                Button(
                    onClick = { isDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.NoteAdd,
                        contentDescription = "Solicitar nuevo apunte",
                        tint = GreenLight
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Solicitar apunte", color = GreenLight)
                }
            }

            // Mostrar archivo adjunto si existe
            formPath?.let {
                val fileName = File(it).name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = GreenLight)
                    Text(
                        text = fileName,
                        color = GreenLight,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.removeAttachment() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Eliminar adjunto",
                            tint = CustomRed
                        )
                    }
                }
            }

            if (attachments.isNotEmpty()) {
                attachments.forEach { path ->
                    val fileName = File(path).name
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Adjunto", tint = GreenLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = fileName, fontSize = 14.sp, color = Color.White, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.removeAttachment() }) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar adjunto", tint = CustomRed)
                        }
                    }
                }
            }


            Spacer(Modifier.height(8.dp))

            // Campo para redactar mensaje
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                TextField(
                    value = message,
                    onValueChange = { viewModel.updateMessage(it) },
                    placeholder = { Text("Redactar mensaje...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.LightGray
                    ),
                    singleLine = false
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Mic con texto abajo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            speechRecognizerManager.startListening()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Dictado por voz",
                            tint = GreenLight
                        )
                    }
                    Text(
                        text = "Dictar",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Diálogo para confirmar salir
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Column {
                            TextButton(
                                onClick = {
                                    viewModel.saveDraft()
                                    showDialog = false
                                    onCancel()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    "Guardar borrador",
                                    color = GreenLight,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            HorizontalDivider(color = Color.Gray)
                            TextButton(
                                onClick = {
                                    viewModel.discardDraft(draftId)
                                    showDialog = false
                                    onCancel()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    "Salir sin guardar",
                                    color = GreenLight,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    title = {
                        Text(
                            "¿Querés salir de la redacción?",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            "Podés guardar este mensaje como borrador para retomarlo más tarde.",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    },
                    containerColor = BlueDark,
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }

            // FormScreen para solictar apunte
            if (isDialogOpen) {
                FormScreen(
                    viewModel,
                    onDismiss = { isDialogOpen = false },
                    onMessageSent = { isDialogOpen = false })
            }
        }
    }
}
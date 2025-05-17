package com.example.proyectofinal.task_student.presentation.view


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.proyectofinal.task_student.presentation.component.AccessibleIconButton
import com.example.proyectofinal.task_student.presentation.component.DownloadOption
import com.example.proyectofinal.task_student.presentation.component.MicControl
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun TaskStudent(navController: NavHostController) {

    val viewModel = koinViewModel<TaskStudentViewModel>()
    val text by viewModel.texto.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val showExtraButtons by viewModel.showExtraButton.collectAsState()
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val isFirstPage by viewModel.isFirstPage.collectAsState()
    val isLastPage by viewModel.isLastPage.collectAsState()

    var rating by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ----- HEADER FIJO -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFFFFA500))
                Text("Volver", color = Color(0xFFFFA500))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccessibleIconButton(
                    icon = Icons.Default.FileDownload,
                    label = "Descargar",
                    onClick = { viewModel.showDownloadDialog() }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Add,
                    label = "Aumentar",
                    onClick = {
                        viewModel.fontSizeIncrease()
                    }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Remove,
                    label = "Disminuir",
                    onClick = {
                        viewModel.fontSizeDecrease()
                    }
                )
            }
        }

        // ----- CUERPO CON BOTÓN FLOTANTE -----
        Box(modifier = Modifier.weight(1f)) {
            // Botón "Escuchar" flotante
            IconButton(
                onClick = {
                    if (!isSpeaking) {
                        viewModel.startSpeech()
                    } else {
                        viewModel.stopSpeech()
                    }
                    viewModel.showExtraButton()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 16.dp)
                    .background(color = Color(0xFFFFA500).copy(alpha = 0.7f), shape = RoundedCornerShape(50))
                    .size(48.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = if (isSpeaking) "Pausar" else "Escuchar",
                    tint = Color.White
                )
            }

            if (showExtraButtons) {
                IconButton(
                    onClick = {
                        if(isPaused) viewModel.resumeSpeech() else viewModel.pauseSpeech()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 80.dp, end = 16.dp)
                        .background(color = if(!isPaused) Color.Red else Color.Green, shape = RoundedCornerShape(50))
                        .size(48.dp)
                        .zIndex(1f)
                ) {
                    Icon(
                        imageVector = if(!isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Pausar",
                        tint = Color.White
                    )
                }
            }

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize
                )
            }
        }

        // ----- FOOTER FIJO -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.previousPage() },
                enabled = currentPageIndex > 0,
                modifier = Modifier
                    .background(
                        if(isFirstPage){
                            Color.Black
                        }
                        else {
                            Color(0xFFFFA500)
                        }
                        , shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint =
                    if(isFirstPage){
                        Color.Black
                    }
                    else {
                        Color.White
                    })
            }

            Text(
                text = "${currentPageIndex + 1} / ${viewModel.totalPages}",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { 
                    if(isLastPage) {
                        Log.d("DEBUG", "Última página: mostrando diálogo")
                        viewModel.showFeedback()
                    }
                    else {
                        viewModel.nextPage()
                    }
                          },
                modifier = Modifier
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(if(isLastPage){
                    Icons.Default.Feedback
                }
                        else {
                    Icons.Default.ArrowForward
                },
                    contentDescription = "Siguiente", tint = Color.White)
            }
        }
    }

    // DIALOG DE DESCARGAS

    if (showDownloadDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Fondo difuminado
                .clickable(onClick = { viewModel.showDownloadDialog() }) // Toca afuera para cerrar
        ) {
            AnimatedVisibility(
                visible = showDownloadDialog,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF4F4F52), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Descargar como:",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    DownloadOption(".MP3", Icons.Default.VolumeUp) {
                        // Acción al presionar MP3
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DownloadOption(".PDF", Icons.Default.MenuBook) {
                        // Acción al presionar PDF
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DownloadOption("Texto Plano", Icons.Default.Description) {
                        // Acción al presionar Texto Plano
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.showDownloadDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6EF6))
                    ) {
                        Text(text = "Cancelar", color = Color.White)
                    }
                }
            }
        }
    }

    // Dialog de feedback
    
    if(showFeedback){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { viewModel.showFeedback() })
        ) {
            AnimatedVisibility(
                visible = showFeedback,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFB6C7D1), RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "¿Qué tal te pareció el apunte?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { index ->
                            IconButton(onClick = {
                                rating = index + 1
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella ${index + 1}",
                                    tint = if (index < rating) Color(0xFFFF6D00) else Color.LightGray,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Mandanos un audio con tu opinión!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    var isRecording by remember { mutableStateOf(false) }
                    var hasRecording by remember { mutableStateOf(false) }
                    var isPlaying by remember { mutableStateOf(false) }
                    var recordingDuration by remember { mutableStateOf(0L) }

                    // Simula el tiempo grabado
                    val timer = rememberCoroutineScope()
                    var playbackTimeLeft by remember { mutableStateOf(0L) }
                    val displayTime = if (isPlaying) playbackTimeLeft else recordingDuration


                    MicControl(
                        isRecording = isRecording,
                        hasRecording = hasRecording,
                        isPlaying = isPlaying,
                        recordingDuration = recordingDuration,
                        onStartRecording = {
                            isRecording = true
                            hasRecording = false
                            recordingDuration = 0L
                            timer.launch {
                                while (isRecording) {
                                    delay(1000)
                                    recordingDuration += 1000
                                }
                            }
                        },
                        onStopRecording = {
                            isRecording = false
                            hasRecording = true
                        },
                        onPlay = {
                            isPlaying = true
                            playbackTimeLeft = recordingDuration
                            timer.launch {
                                while (playbackTimeLeft > 0 && isPlaying) {
                                    delay(1000)
                                    playbackTimeLeft -= 1000
                                }
                                isPlaying = false
                                playbackTimeLeft = recordingDuration
                            }
                        },
                        onStop = {
                            isPlaying = false
                        },
                        onDiscard = {
                            hasRecording = false
                            isPlaying = false
                            recordingDuration = 0L
                            playbackTimeLeft = 0L
                            //poner pa borrar archivo
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.showFeedback()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6EF6))
                    ) {
                        Text(text = "Enviar opinión", color = Color.White)
                    }

                }
            }
        }
    }
}

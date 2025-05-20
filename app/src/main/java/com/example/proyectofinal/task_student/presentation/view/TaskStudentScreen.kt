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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    val showFont by viewModel.showFont.collectAsState()
    val showAnnotations by viewModel.showAnnotations.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val isFirstPage by viewModel.isFirstPage.collectAsState()
    val isLastPage by viewModel.isLastPage.collectAsState()

    var rating by remember { mutableStateOf(0) }

    var isRecording by remember { mutableStateOf(false) }
    var hasRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0L) }

    // Simula el tiempo grabado
    val timer = rememberCoroutineScope()
    var playbackTimeLeft by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ----- HEADER FIJO -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccessibleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = "Volver",
                    onClick = { navController.popBackStack() }
                )
                AccessibleIconButton(
                    icon = Icons.Default.FileDownload,
                    label = "Descargar",
                    onClick = { viewModel.showDownloadDialog() }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Create,
                    label = "Anotaciones",
                    onClick = {
                        viewModel.showAnnotations()
                    }
                )
                AccessibleIconButton(
                    icon = Icons.Default.FontDownload,
                    label = "Fuente",
                    onClick = {
                        viewModel.showFont()
                    }
                )
                AccessibleIconButton(
                    icon = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    label = if (isSpeaking) "Pausar" else "Escuchar",
                    onClick = {
                        if (!isSpeaking) {
                            viewModel.startSpeech()
                        } else {
                            viewModel.stopSpeech()
                        }
                        viewModel.showExtraButton()
                    }
                )
            }
        }

            // Contenido scrolleable
        Box(modifier = Modifier.weight(1f)){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize,
                    lineHeight = fontSize * 1.5f,
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
                        if (isFirstPage) {
                            Color.Black
                        } else {
                            Color(0xFFFFA500)
                        }, shape = RoundedCornerShape(8.dp)
                    )
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

    // dialog anotaciones

    if(showAnnotations){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    viewModel.showAnnotations()
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {} // evita cierre si se toca adentro
            ) {
                Text(
                    text = "Tus anotaciones",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CenteredAudioBar(
                    isPlaying = isPlaying,
                    onPlayPauseClick = { isPlaying = !isPlaying }
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.showAnnotations() },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    // dialog fuente

    if(showFont){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .background(Color.Black.copy(alpha = 0.5f)) // Fondo difuminado
                .clickable(onClick = { viewModel.showFont() }) // Toca afuera para cerrar
        ){
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccessibleIconButton(
                        icon = Icons.Default.FontDownload,
                        label = "Fuente",
                        onClick = {

                        },
                        iconSize = 56.dp
                    )
                    AccessibleIconButton(
                        icon = Icons.Default.TextIncrease,
                        label = "Aumentar",
                        onClick = {
                            viewModel.fontSizeIncrease()
                        },
                        iconSize = 56.dp
                    )
                    AccessibleIconButton(
                        icon = Icons.Default.TextDecrease,
                        label = "Disminuir",
                        onClick = {
                            viewModel.fontSizeDecrease()
                        },
                        iconSize = 56.dp
                    )
                }
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



@Composable
fun CenteredAudioBar(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    elapsedTime: String = "00:35" // podés cambiarlo por un valor real luego
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ▶️ Play/Pause
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 📈 Barra de audio que ocupa el espacio restante
            AudioVisualizer(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ⏱️ Timer
            Text(
                text = elapsedTime,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AudioVisualizer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barHeights = listOf(10, 20, 30, 20, 10, 25, 15, 30, 18, 12)
        barHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .padding(horizontal = 1.dp)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(2.dp))
            )
        }
    }
}
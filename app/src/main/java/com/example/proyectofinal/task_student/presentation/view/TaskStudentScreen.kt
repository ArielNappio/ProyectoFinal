package com.example.proyectofinal.task_student.presentation.view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proyectofinal.task_student.presentation.component.AccessibleIconButton
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import com.example.proyectofinal.task_student.presentation.component.DownloadOption


@Composable
fun TaskStudent() {

    val viewModel = koinViewModel<TaskStudentViewModel>()
    val texto by viewModel.texto.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val showExtraButtons by viewModel.showExtraButton.collectAsState()
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

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
            TextButton(onClick = { /* volver */ }) {
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
                    .background(color = Color(0xFFFFA500), shape = RoundedCornerShape(50))
                    .size(48.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.PauseCircleOutline else Icons.Default.VolumeUp,
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
                    text = texto,
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
                onClick = { /* página anterior */ },
                modifier = Modifier
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint = Color.White)
            }

            Text(
                text = "1",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { /* página siguiente */ },
                modifier = Modifier
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente", tint = Color.White)
            }
        }
    }

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
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

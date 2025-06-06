package com.example.proyectofinal.task_student.presentation.component


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay

@Composable
fun MicControl(
    isRecording: Boolean,
    hasRecording: Boolean,
    isPlaying: Boolean,
    recordingDuration: Long, // en milisegundos
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onDiscard: () -> Unit
) {
    val wavePoints = remember { mutableStateListOf<Float>() }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                if (wavePoints.size > 30) wavePoints.removeAt(0)
                wavePoints.add((10..30).random().toFloat())
                delay(50)
            }
        } else {
            wavePoints.clear()
        }
    }

    val formattedTime = remember(recordingDuration) {
        val seconds = (recordingDuration / 1000) % 60
        val minutes = (recordingDuration / 1000) / 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                when {
                    isRecording -> Color.Red
                    hasRecording -> Color(0xFF4CAF50) // Verde si ya grabó
                    else -> Color(0xFF3B6EF6) // Azul normal
                }
            )
            .pointerInput(Unit) {
                if (!hasRecording) {
                    detectTapGestures(
                        onPress = {
                            onStartRecording()
                            tryAwaitRelease()
                            onStopRecording()
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Canvas de onda si está grabando
        if (isRecording) {
            Canvas(modifier = Modifier
                .matchParentSize()
                .alpha(0.3f)
            ) {
                val path = Path()
                val widthStep = size.width / (wavePoints.size.coerceAtLeast(1))
                wavePoints.forEachIndexed { index, point ->
                    val x = index * widthStep
                    val y = size.height / 2 + point
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, Color.White, style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }

        // CONTENIDO DEPENDIENDO DEL ESTADO
        when {
            isRecording -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grabando...",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 16.dp)
                    )
                }
            }

            hasRecording -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (isPlaying) {
                            onStop()
                        } else {
                            onPlay()
                        }
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Reproducir/Pausar",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = formattedTime,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    IconButton(onClick = onDiscard) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Borrar",
                            tint = Color.White
                        )
                    }
                }
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mantener para grabar",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicPermissionWrapper(
    content: @Composable () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    when {
        permissionState.status.isGranted -> {
            content()
        }
        permissionState.status.shouldShowRationale -> {
            PermissionRationale {
                permissionState.launchPermissionRequest()
            }
        }
        else -> {
            onPermissionDenied()
        }
    }
}



@Composable
fun PermissionRationale(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
            Text("Este permiso es necesario para grabar audio")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Conceder permiso")
            }
    }
}

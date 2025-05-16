package com.example.proyectofinal.task_student.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
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

@Composable
fun MicButtonWithWave(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wavePoints = remember { mutableStateListOf<Float>() }

    // Simulación simple de la onda (se puede reemplazar con datos reales)
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                if (wavePoints.size > 30) wavePoints.removeAt(0)
                wavePoints.add((10..30).random().toFloat()) // valores random para simular onda
                kotlinx.coroutines.delay(50)
            }
        } else {
            wavePoints.clear()
        }
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isRecording) Color.Red else Color(0xFF3B6EF6))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onStartRecording()
                        tryAwaitRelease()
                        onStopRecording()
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Canvas para onda - ocupa TODO el Box, pero se dibuja semi-transparente para no tapar texto
        if (isRecording) {
            Canvas(modifier = Modifier
                .matchParentSize()
                .alpha(0.3f) // onda semi-transparente
            ) {
                val path = Path()
                val widthStep = size.width / (wavePoints.size.coerceAtLeast(1))
                wavePoints.forEachIndexed { index, point ->
                    val x = index * widthStep
                    val y = size.height / 2 + point // centro vertical + valor
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, Color.White, style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isRecording) "Grabando..." else "Mantener para grabar",
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
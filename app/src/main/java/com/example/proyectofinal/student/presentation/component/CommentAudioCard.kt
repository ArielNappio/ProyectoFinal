package com.example.proyectofinal.student.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.util.formatDuration

@Composable
fun CommentAudioCard(
    comment: RecordedAudio,
    isPlaying: Boolean,
    currentPosition: Long,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSeek: (Long, Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDuration = comment.duration.coerceAtLeast(1L)
    val formattedRemainingTime = formatDuration(comment.duration - currentPosition)
    val sliderPosition = remember(currentPosition) { mutableStateOf(currentPosition.toFloat()) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(comment.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Text(
                text = formattedRemainingTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (isPlaying) onPauseClick() else onPlayClick()
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                    )
                }

                Slider(
                    value = sliderPosition.value,
                    onValueChange = { newValue ->
                        sliderPosition.value = newValue
                    },
                    onValueChangeFinished = {
                        onSeek(sliderPosition.value.toLong(), true, comment.filePath)
                    },
                    valueRange = 0f..totalDuration.toFloat(),
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )

                )
                IconButton(onClick = {
                    onDeleteClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar"
                    )
                }
            }
        }
    }
}


package com.example.proyectofinal.student.presentation.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.student.presentation.component.CommentAudioCard
import com.example.proyectofinal.student.presentation.viewmodel.CommentsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    taskId: Int
) {
    val viewModel = koinViewModel< CommentsViewModel>()

    val comments by viewModel.comments.collectAsState()
    val currentlyPlayingPath by viewModel.currentlyPlayingPath.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        CommentsTopBar(
            title = "Comentarios en audio",
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (comments.isEmpty()) {
            Text(
                "No hay comentarios aún.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comments.size) { index ->
                    val comment = comments[index]
                    CommentAudioCard(
                        comment = comment,
                        isPlaying = isPlaying && comment.filePath == currentlyPlayingPath,
                        currentPosition = if (currentlyPlayingPath == comment.filePath) currentPosition else 0L,
                        onPlayClick = {
                            println("Reproduciendo ${comment.filePath}")
                            viewModel.playAudio(comment.filePath)
                        },
                        onPauseClick = { viewModel.playAudio(comment.filePath) },
                        onSeek = { position, playAfterSeek, path ->
                            viewModel.seekTo(position, playAfterSeek, path)
                        },
                        onDeleteClick = {
                            viewModel.deleteComment(comment.filePath)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info extra abajo de cada card
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(
                            text = "📄 Página: ${comment.page}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "📅 Fecha: ${comment.date}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CommentsTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón volver con ícono grande y texto abajo
        Column(
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Volver",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Título centrado (flexible)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )
        }
    }
}

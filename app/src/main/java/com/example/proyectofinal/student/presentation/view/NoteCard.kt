package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.student.data.model.Note
import com.example.proyectofinal.core.theme.CustomBlue
import com.example.proyectofinal.core.theme.CustomOrange

@Composable
fun NoteCard(note: Note, onToggleFavorite: (Int) -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        border = BorderStroke(4.dp, CustomBlue),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = note.name
                        }
                )
                IconButton(
                    onClick = { onToggleFavorite(note.id) },
                    modifier = Modifier.semantics {
                        contentDescription = if (note.isFavorite)
                            "Quitar de favoritos"
                        else
                            "Agregar a favoritos"
                    }
                ) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (note.isFavorite) CustomOrange else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Text(
                text = note.description,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics {
                    contentDescription = note.description
                }
            )

        }
    }
}

@Preview (showBackground = true)
@Composable
fun NoteCardPreview() {
    val note = Note(1, "Título del apunte", "Descripción del apunte", false)
    NoteCard(note = note, onToggleFavorite = {})
}

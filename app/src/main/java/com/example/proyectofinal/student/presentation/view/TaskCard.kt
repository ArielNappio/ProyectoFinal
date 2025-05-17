package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.student.data.model.Task
import com.example.proyectofinal.ui.theme.CustomBlue
import com.example.proyectofinal.ui.theme.CustomOrange

@Composable
fun TaskCard(task: Task, onToggleFavorite: (Int) -> Unit, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("${ScreensRoute.TaskDetails.route}/${task.id}") },
        border = BorderStroke(4.dp, CustomBlue),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = task.name
                        }
                )
                IconButton(
                    onClick = { onToggleFavorite(task.id) },
                    modifier = Modifier.semantics {
                        contentDescription = if (task.isFavorite)
                            "Quitar de favoritos"
                        else
                            "Agregar a favoritos"
                    }
                ) {
                    Icon(
                        imageVector = if (task.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (task.isFavorite) CustomOrange else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Text(
                text = task.description,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics {
                    contentDescription = task.description
                }
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCardPreview() {
    val task = Task(1, "Título del apunte", "Descripción del apunte", false)
    TaskCard(task = task, onToggleFavorite = {}, navController = NavHostController(LocalContext.current))
}

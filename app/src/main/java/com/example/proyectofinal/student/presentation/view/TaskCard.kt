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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.core.theme.CustomBlue
import com.example.proyectofinal.core.theme.CustomOrange
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.student.data.model.Task

@Composable
fun TaskCard(task: Task, onToggleFavorite: (Int) -> Unit, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("${ScreensRoute.TaskDetails.route}/${task.id}") },
        border = BorderStroke(4.dp, CustomBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = task.name
                        },
                    fontSize = 26.sp

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
                text = "📅 Ultima vez leído: ${task.lastRead ?: "Sin leer"}",
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "📄 ${task.pageCount ?: 0} pág.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (task.hasComments == true) {
                    Text(
                        text = "💬 Hay comentarios",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                else {
                    Text(
                        text = "💬 No hay comentarios",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TaskCardPreview() {
//    val task = Task(1, "Título del apunte", "Descripción del apunte", false)
//    TaskCard(task = task, onToggleFavorite = {}, navController = NavHostController(LocalContext.current))
//}

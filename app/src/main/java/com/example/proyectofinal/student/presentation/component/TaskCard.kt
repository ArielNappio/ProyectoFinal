package com.example.proyectofinal.student.presentation.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.core.theme.CustomOrange
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.userpreferences.presentation.component.AppText

@Composable
fun TaskCard(task: Task, onToggleFavorite: (Int) -> Unit, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("${ScreensRoute.TaskDetails.route}/${task.id}") },
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppText(
                    text = task.name,
                    isTitle = true,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    //style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = task.name
                        }
                        .fillMaxWidth(),
                    //fontSize = 26.sp

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
            AppText(
                text = "📅 Última vez leído: ${task.lastRead ?: "Sin leer"}",
                maxLines = 2,
                //style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                //fontSize = 18.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AppText(
                    text = "📄 ${task.pageCount ?: 0} pág.",
                    //style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (task.hasComments) {
                    AppText(
                        text = "💬 Hay comentarios",
                        //style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                else {
                    AppText(
                        text = "💬 No hay comentarios",
                        //style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

        }
    }
}
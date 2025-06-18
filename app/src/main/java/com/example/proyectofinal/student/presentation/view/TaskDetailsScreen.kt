package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.core.theme.CustomBlue
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.student.presentation.viewmodel.DetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskDetailScreen(
    modifier: Modifier,
    taskId: Int,
    navController: NavController
) {
    val detailViewModel = koinViewModel<DetailsViewModel>()
    val task by detailViewModel.task.collectAsState()

    val fontSizeText by detailViewModel._fontSizeText.collectAsState()
    val fontSizeTitle by detailViewModel._fontSizeTitle.collectAsState()

    LaunchedEffect(taskId) {
        println("taskId recibido: $taskId")
        detailViewModel.getTaskById(taskId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        task?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .border(2.dp, CustomBlue, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .semantics {
                        heading()
                        contentDescription = "Detalle de tarea"
                    }
            ) {
                Text(
                    text = task!!.name,
                    color = CustomBlue,
                    fontSize = fontSizeTitle,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics {
                        contentDescription = task!!.name
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = task!!.description,
                    fontSize = fontSizeText,
                    modifier = Modifier.semantics {
                        contentDescription = task!!.description
                    }
                )

                Column(modifier = Modifier.padding(top = 8.dp)) {
                    task!!.lastRead?.let { date ->
                        Text(
                            text = "📅 Última lectura: $date",
                            fontSize = fontSizeText
                        )
                    }
                    task!!.pageCount?.let { pages ->
                        Text(
                            text = "📄 Cantidad de páginas: $pages",
                            fontSize = fontSizeText
                        )
                    }
                    if (task!!.hasComments == true) {
                        Text(
                            text = "💬 Esta tarea tiene comentarios",
                            fontSize = fontSizeText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (task!!.hasComments == true) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            navController.navigate("${ScreensRoute.Comments.route}/$taskId")
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .semantics {
                                contentDescription = "Botón para ver comentarios en audio"
                            },
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver comentarios", fontSize = fontSizeText)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .semantics {
                            contentDescription = "Botón para ver el apunte en la app"
                        },
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver apunte en la app", fontSize = fontSizeText)
                }
            }
        } ?: Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CustomBlue)
        }
    }
}
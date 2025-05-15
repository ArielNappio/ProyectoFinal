package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.student.presentation.viewmodel.DetailsViewModel
import com.example.proyectofinal.ui.theme.CustomBlue
import com.example.proyectofinal.ui.theme.CustomOrange
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskDetailScreen(
    modifier: Modifier,
    taskId: Int,
    onBackClick: () -> Unit,
    onViewNoteClick: () -> Unit
) {
    val detailViewModel = koinViewModel<DetailsViewModel>()
    val task by detailViewModel.task.collectAsState()

    LaunchedEffect(taskId) {
        println("taskId recibido: $taskId")
        detailViewModel.getNoteById(taskId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Button(
            onClick = onBackClick,
            modifier = modifier.semantics {
                contentDescription = "Volver a la pantalla anterior"
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, CustomOrange)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = CustomOrange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = CustomOrange)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (task != null) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.semantics {
                        contentDescription = task!!.name
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = task!!.description,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = modifier.semantics {
                        contentDescription = task!!.description
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onViewNoteClick,
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .semantics {
                            contentDescription = "Botón para ver el apunte en la app"
                        },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver apunte en la app", color = Color.White)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CustomBlue)
            }
        }
    }
}
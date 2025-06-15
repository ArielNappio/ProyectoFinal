package com.example.proyectofinal.student.presentation.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.orderManagment.domain.model.TaskGroup

@Composable
fun TaskGroupCard(
    taskGroup: TaskGroup,
    onToggleFavorite: (Int) -> Unit,
    navController: NavController
) {
    Column {
        Text(
            text = taskGroup.title,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    Log.d("TaskGroupCard", "Click en grupo: ${taskGroup.title}")
                }
        )
        taskGroup.tasks.forEach { task ->
            TaskCard(
                task = task,
                onToggleFavorite = onToggleFavorite,
                navController = navController
            )
        }
    }
}
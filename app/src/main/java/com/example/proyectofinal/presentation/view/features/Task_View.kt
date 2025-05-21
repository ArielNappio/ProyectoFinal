package com.example.proyectofinal.presentation.view.features

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import okhttp3.internal.concurrent.Task

@Preview
@Composable
fun TaskListScreen() {
    val taskList = remember { mutableStateListOf<Task>() }
    var counter = remember { mutableStateOf(1) }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            onAddClick = {
            }
        )
        Body(taskList = taskList)
    }
}



@Composable
fun Header(onAddClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lista de tareas",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar tarea")
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.Black
        )
    }
}


@Composable
fun Body(taskList: List<Task>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
      {

      }
}






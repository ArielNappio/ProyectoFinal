package com.example.proyectofinal.student.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.student.presentation.component.TaskCard
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val tasks by viewModel.tasks.collectAsState()

    val context = LocalContext.current
    var isDialogOpen by remember { mutableStateOf(false) }

    // Form state
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var career by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var searchText by remember { mutableStateOf("") }

                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar palabras clave"
                        )
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filtrar",
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(tasks) {
                    TaskCard(
                        task = it,
                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                        navController = navController
                    )
                }
            }
        }
        // Floating Action Button
        FloatingActionButton(
            onClick = { isDialogOpen = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp),
            containerColor = Color(0xFF00934F)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Solicitar nuevo apunte")
        }

        // Modal Dialog for New Document
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Solicitar nuevo apunte") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") })
                        OutlinedTextField(value = career, onValueChange = { career = it }, label = { Text("Carrera") })
                        OutlinedTextField(value = signature, onValueChange = { signature = it }, label = { Text("Cátedra") })
                        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha") })
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isDialogOpen = false
                            Toast.makeText(context, "Documento solicitado correctamente", Toast.LENGTH_SHORT).show()

                            // Clear form after submitting
                            title = ""
                            author = ""
                            career = ""
                            signature = ""
                            date = ""
                        }
                    ) {
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDialogOpen = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}


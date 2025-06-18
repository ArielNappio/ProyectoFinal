package com.example.proyectofinal.student.presentation.view

import LoadingWithImageBar
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val orderState by viewModel.orderManagementState.collectAsState()

    Log.d("HomeScreen", "OrderState: $orderState")

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when (val orders = orderState) {
                is NetworkResponse.Loading -> {
                    LoadingWithImageBar(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        imageResId = R.drawable.wirin_logo_dark,
                        loadingTexts = listOf(
                            "Creando libros...",
                            "Fabricando historias...",
                            "Leyendo mentes...",
                            "Preparando aventuras...",
                            "Hablando con los personajes...",
                            "Están llegando...",
                            "Cultivando ideas...",
                            "Criando animalitos...",
                            "Rompiendo leyes..."
                        )
                    )
                }

                is NetworkResponse.Success -> {
                    val projects = orders.data ?: emptyList()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(projects) { project ->
                            ProjectCard(
                                project = project,
                                onClick = {
                                    navController.navigate("project_detail/${project.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                is NetworkResponse.Failure -> {
                    Text(
                        text = "Error al cargar proyectos: ${orders.error}",
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ProjectCard(
    project: OrderDelivered,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${project.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "${project.orders.size} tareas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

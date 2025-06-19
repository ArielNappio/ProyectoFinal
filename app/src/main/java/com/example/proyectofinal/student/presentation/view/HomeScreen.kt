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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.proyectofinal.userpreferences.presentation.component.AppText
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
                            "Rompiendo leyes...",
                            "Acariciando gatos..."
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
                    AppText(
                        text = "Error al cargar proyectos: ${orders.error}"
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
    val formattedTitle = project.title.replace(Regex(""" (\S+)$"""), "\u00A0$1")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título + favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = formattedTitle,
                    isTitle = true,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tareas con ícono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Tareas",
                    tint = Color(0xFFB0BEC5)
                )
                Spacer(modifier = Modifier.width(6.dp))
                AppText(
                    text = "${project.orders.size}",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))


                IconButton(onClick = { /* toggle favorito acá */ }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        modifier = Modifier.size(36.dp),
                        contentDescription = "Favorito",
                        tint = Color.Yellow
                    )
                }
            }
        }
    }
}

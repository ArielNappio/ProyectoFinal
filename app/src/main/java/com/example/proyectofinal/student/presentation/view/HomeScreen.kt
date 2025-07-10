package com.example.proyectofinal.student.presentation.view

import LoadingWithImageBar
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.student.presentation.component.ProjectCard
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val orderState by viewModel.orderManagementState.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()

    Log.d("HomeScreen", "OrderState: $orderState")

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            when {
                isLoading -> {
                    LoadingWithImageBar(
                        loadingTexts = listOf(
                            "Creando libros...",
                            "Fabricando historias...",
                            "Leyendo mentes...",
                            "Preparando aventuras..."
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                orders.isEmpty() -> {
                    AppText(text = "No hay proyectos para mostrar")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(orders) { project ->
                            ProjectCard(
                                project = project,
                                onClick = {
                                    navController.navigate("project_detail/${project.id}")
                                },
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(project.id, !project.isFavorite)
                                },
                                iconSize = iconSize
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

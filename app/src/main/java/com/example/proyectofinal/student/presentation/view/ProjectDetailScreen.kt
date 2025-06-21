package com.example.proyectofinal.student.presentation.view

import LoadingWithImageBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import com.example.proyectofinal.student.presentation.component.ProjectCard
import com.example.proyectofinal.student.presentation.viewmodel.ProjectDetailViewModel
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<ProjectDetailViewModel>()
    val projectState by viewModel.projectState.collectAsState()

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Proyecto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val project = projectState) {
                is NetworkResponse.Loading -> {
                    LoadingWithImageBar(
                        modifier = Modifier.fillMaxSize(),
                        loadingTexts = listOf(
                            "Cargando proyecto...",
                            "Obteniendo detalles...",
                            "Preparando contenido..."
                        )
                    )
                }

                is NetworkResponse.Success -> {
                    project.data?.let { projectData ->
                        ProjectDetailContent(
                            project = projectData,
                            onViewInApp = { taskId ->
                                navController.navigate("${ScreensRoute.Task.route}/$taskId")
                            },
                            onViewAnnotations = { taskId ->
                                navController.navigate("${ScreensRoute.Comments.route}/$taskId")
                            },
                            onToggleFavorite = { projectId, isFavorite ->
                                viewModel.toggleFavorite(
                                    project.data.id.toString(),
                                    !project.data.isFavorite
                                )
                            }
                        )
                    }
                }

                is NetworkResponse.Failure -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error al cargar el proyecto",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project.error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectDetailContent(
    project: OrderDelivered,
    onViewInApp: (Int) -> Unit,
    onViewAnnotations: (Int) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProjectCard(project = project,
                onClick = {},
                onToggleFavorite ={ onToggleFavorite(project.id.toString(), project.isFavorite) })
        }

        item {
            Text(
                text = "Tareas del Proyecto",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(project.orders) { task ->
            TaskItemCard(
                task = task,
                onViewInApp = { onViewInApp(task.id) },
                onViewAnnotations = { onViewAnnotations(task.id) }
            )
        }
    }
}
@Composable
fun TaskItemCard(
    task: OrderStudent,
    onViewInApp: () -> Unit,
    onViewAnnotations: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 🟡 Título
            AppText(
                text = task.name,
                isTitle = true,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🟣 Páginas leídas
            Row(
                modifier = Modifier.fillMaxWidth(),
            ){
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Paginas",
                    tint = Color(0xFFFFC107)
                )
                AppText(
                    text = "${task.lastRead ?: 0}/${task.pageCount ?: 0} Leídas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(6.dp))

            // 🔵 Botones (ver en app / anotaciones)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 📝 Botón de anotaciones (solo si tiene)
                if (task.hasComments) {
                    Column(
                        modifier = Modifier
                            .clickable { onViewAnnotations() }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AudioFile, // ✏️
                            contentDescription = "Ver anotaciones",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // ▶️ Botón de ver en app
                Column(
                    modifier = Modifier
                        .clickable { onViewInApp() }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Ver en app",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}
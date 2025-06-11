package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.student.presentation.component.TaskCard
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val state by viewModel.orderManagmentState.collectAsState()

    val favoriteTasks = when (state) {
        is NetworkResponse.Success -> {
            (state as NetworkResponse.Success<List<Task>>).data?.filter { it.isFavorite }
        }
        else -> emptyList()
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (favoriteTasks != null) {
                if (favoriteTasks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(text = "Acá deberían estar tus favoritos :(", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.wirin_logo_dark),
                            contentDescription = "Logo de Wirin",
                            modifier = Modifier.size(186.dp)
                        )
                        Text(
                            text = "😢",
                            fontSize = 48.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoriteTasks) { task ->
                            TaskCard(
                                task = task,
                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


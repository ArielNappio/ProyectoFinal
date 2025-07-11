package com.example.proyectofinal.student.presentation.view

import LoadingWithImageBar
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.audio.speechrecognizer.SpeechRecognizerManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.student.presentation.component.ProjectCard
import com.example.proyectofinal.student.presentation.component.SearchBar
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val context = LocalContext.current
    val orderState by viewModel.orderManagementState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    val speechRecognizerManager = remember {
        SpeechRecognizerManager(
            context = context,
            onResult = { result ->
                viewModel.updateSearchText(result)
            },
            onError = { error ->
                Log.e("SpeechRecognizer", error)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerManager.stopListening()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.wirin_25),
                        contentDescription = "Logo de Wirin",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )

        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                searchText = searchText,
                onTextChange = { viewModel.updateSearchText(it) },
                onVoiceClick = { speechRecognizerManager.startListening() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (val orders = orderState) {
                is NetworkResponse.Loading -> {
                    LoadingWithImageBar(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        imageResId = R.drawable.wirin_logo_dark,
                        loadingTexts = listOf(
                            "Buscando historias...",
                            "Leyendo mentes...",
                            "Cargando aventuras..."
                        )
                    )
                }

                is NetworkResponse.Success -> {
                    val filteredProjects = orders.data?.filter {
                        it.title.contains(searchText, ignoreCase = true)
                    } ?: emptyList()

                    if (filteredProjects.isEmpty()) {
                        Text(
                            text = "No se encontraron resultados para \"$searchText\"",
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredProjects) { project ->
                                ProjectCard(
                                    project = project,
                                    onClick = {
                                        navController.navigate("project_detail/${project.id}")
                                    },
                                    onToggleFavorite = {},
                                    iconSize = 24.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                is NetworkResponse.Failure -> {
                    Text(
                        text = "Error al cargar proyectos: ${orders.error}",
                        color = Color.White,
                    )
                }
            }
        }
    }
}



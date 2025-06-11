package com.example.proyectofinal.student.presentation.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.audio.speechrecognizer.SpeechRecognizerManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.student.presentation.component.SearchBar
import com.example.proyectofinal.student.presentation.component.TaskCard
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    //val tasks by viewModel.tasks.collectAsState()
    val orderState by viewModel.orderManagmentState.collectAsState()
    Log.d("HomeScreen", "OrderState: $orderState")

    val context = LocalContext.current

    // Speech recognizer
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

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(
                searchText = searchText,
                onTextChange = { viewModel.updateSearchText(it) },
                onVoiceClick = { speechRecognizerManager.startListening() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = orderState) {
                is NetworkResponse.Loading -> {
                    Text("Cargando órdenes...")
                }

                is NetworkResponse.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data!!) { task: Task ->
                            TaskCard(
                                task = task,
                                onToggleFavorite = { /*id -> viewModel.toggleFavorite(id)*/ },
                                navController = navController
                            )
                        }
                    }
                }

                is NetworkResponse.Failure -> {
                    Text("Error al cargar órdenes: ${state.error}")
                }
            }

        }
    }
}

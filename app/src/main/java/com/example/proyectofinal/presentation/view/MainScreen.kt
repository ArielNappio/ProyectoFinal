package com.example.proyectofinal.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.presentation.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.proyectofinal.presentation.viewmodel.UiState

@Composable
fun MainScreen(
    navController: NavController,
) {
    val viewModel = koinViewModel<MainViewModel>()
    val userState by viewModel.userState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        when (userState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UiState.Success -> {
                val user = (userState as UiState.Success).data
                user?.let {
                    Text("Welcome, ${it.fullName}")
                    Text("Email: ${it.email}")
                }
            }
            is UiState.Error -> {
                val errorMessage = (userState as UiState.Error).message
                Text("Error: $errorMessage", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de navegación
        Button(onClick = { navController.navigate("camera") }, modifier = Modifier.fillMaxWidth()) {
            Text("Go to camera screen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("api") }, modifier = Modifier.fillMaxWidth()) {
            Text("Go to API consumption screen")
        }
    }
}

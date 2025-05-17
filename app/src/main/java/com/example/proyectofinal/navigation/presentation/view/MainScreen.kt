package com.example.proyectofinal.navigation.presentation.view

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.core.util.UiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainScreenUiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    navController: NavController,
) {
    val viewModel = koinViewModel<MainViewModel>()
    val userState by viewModel.userState.collectAsState()
    val mainScreenUiState by viewModel.mainScreenUiState.collectAsState()

    when (mainScreenUiState) {
        is MainScreenUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cargando...")
                CircularProgressIndicator()
            }
        }
        is MainScreenUiState.Authenticated -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (userState) {
                    is UiState.Loading -> {
                        Text("Cargando datos del usuario...")
                    }
                    is UiState.Success -> {
                        val user = (userState as UiState.Success).data
                        user?.let {
                            Text("Username: ${it.userName}")
                            Text("Welcome, ${it.fullName}")
                            Text("Email: ${it.email}")
                            Text("Rol: ${it.roles[0]}")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { navController.navigate("camera") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Go to camera screen")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
                            Text("logout")
                        }
                    }
                    is UiState.Error -> {
                        val errorMessage = (userState as UiState.Error).message
                        Text("Error al cargar datos del usuario: $errorMessage", color = Color.Red)
                        Log.d("DATA STATE","Error al cargar datos del usuario")
                    }
                }
            }
        }
        is MainScreenUiState.Unauthenticated -> {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }
}



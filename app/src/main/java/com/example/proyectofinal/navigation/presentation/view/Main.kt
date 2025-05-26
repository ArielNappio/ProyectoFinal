package com.example.proyectofinal.navigation.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectofinal.navigation.NavigationComponent
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.navigation.component.BottomBar
import com.example.proyectofinal.navigation.component.TopBar
import com.example.proyectofinal.navigation.presentation.viewmodel.MainScreenUiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
import com.example.proyectofinal.navigation.util.showsBottomBar
import com.example.proyectofinal.navigation.util.showsTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun Main(
    modifier: Modifier,
    navController: NavHostController,
) {
    val viewModel = koinViewModel<MainViewModel>()
    val mainScreenUiState by viewModel.mainScreenUiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
            LaunchedEffect(Unit) {
                navController.navigate(ScreensRoute.Home.route) {
                    popUpTo(ScreensRoute.Login.route) { inclusive = true }
                    popUpTo(ScreensRoute.Home.route) { inclusive = true }
                }
            }
        }

        is MainScreenUiState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                navController.navigate(ScreensRoute.Home.route) {
                    popUpTo(ScreensRoute.Login.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = { if (navBackStackEntry?.showsTopBar() == true) TopBar(navController) },
        bottomBar = { if (navBackStackEntry?.showsBottomBar() == true) BottomBar(navController) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavigationComponent(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

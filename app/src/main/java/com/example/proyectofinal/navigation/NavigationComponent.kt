package com.example.proyectofinal.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinal.R
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.student.presentation.view.FavoritesScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.StudentProfileScreen

@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    Scaffold(
        topBar = { AppHeader() },
        modifier = Modifier
            .fillMaxSize()
//            .background(LocalTheme.current.colorScheme.background)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreensRoute.Login.route,
            route = "root",
            modifier = Modifier
                .padding(innerPadding)
//                .background(LocalTheme.current.colorScheme.background)
                .padding(16.dp)
        ) {
            composable(route = ScreensRoute.Login.route) {
                LoginScreen(navController)
            }
            composable(route = ScreensRoute.Home.route) {
                HomeScreen()
            }
            composable(route = ScreensRoute.Favorites.route) {
                FavoritesScreen()
            }
            composable(route = ScreensRoute.Profile.route) {
                StudentProfileScreen(themeViewModel)
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(LocalTheme.current.colorScheme.background)
            .padding(top = 30.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "¡Bienvenido!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
//            color = LocalTheme.current.colorScheme.onBackground,
            modifier = Modifier.semantics { contentDescription = "Bienvenido" }
        )
        Image(
            painter = painterResource(id = R.drawable.wirin_logo),
            contentDescription = "Logo de Wirin",
            modifier = Modifier.size(56.dp)
        )
    }
}
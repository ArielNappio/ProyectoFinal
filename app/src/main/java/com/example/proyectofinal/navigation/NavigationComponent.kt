package com.example.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen

//import com.example.proyectofinal.auth.presentation.view.LoginScreen

@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = ScreensRoute.Home.route) {
        composable(route = ScreensRoute.Login.route) {
            LoginScreen(navController)
        }
        composable(route = ScreensRoute.Home.route) {
            HomeScreen(navController)
        }
        composable(route = ScreensRoute.Favorites.route) {
//            FavoritesScreen()
        }
        composable(route = ScreensRoute.Profile.route) {
//            ProfileScreen()
        }
    }
}

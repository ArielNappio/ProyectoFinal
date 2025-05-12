package com.example.proyectofinal.navigation

sealed class ScreensRoute(val route: String) {
    object Login : ScreensRoute("login")
    data object Home : ScreensRoute("home")
    data object Favorites : ScreensRoute("favorites")
    data object Profile : ScreensRoute("profile")
}
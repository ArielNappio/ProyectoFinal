package com.example.proyectofinal.navigation.util

import androidx.navigation.NavBackStackEntry
import com.example.proyectofinal.navigation.ScreensRoute


private fun NavBackStackEntry.hasProperty(predicate: (ScreensRoute) -> Boolean): Boolean {
    val route = destination.route ?: return false
    return ScreensRoute.entries.any {
        route.startsWith(it.route) && predicate(it)
    }
}

fun NavBackStackEntry.showsBottomBar(): Boolean = hasProperty { it.showsBottomBar }

fun NavBackStackEntry.showsTopBar(): Boolean = hasProperty { it.showsTopBar }

fun NavBackStackEntry.showBackButton(): Boolean = hasProperty { it.showsBackButton }
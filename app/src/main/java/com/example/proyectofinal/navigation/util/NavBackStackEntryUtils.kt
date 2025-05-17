package com.example.proyectofinal.navigation.util

import androidx.navigation.NavBackStackEntry
import com.example.proyectofinal.navigation.ScreensRoute


private fun NavBackStackEntry.hasProperty(predicate: (ScreensRoute) -> Boolean): Boolean {
    val route = destination.route ?: return false
    return ScreensRoute.entries.any {
        route.startsWith(it.route) && predicate(it)
    }
}

fun NavBackStackEntry.showsBottomBar(): Boolean {
    return hasProperty { it.showsBottomBar }
}

fun NavBackStackEntry.showsTopBar(): Boolean {
    return hasProperty { it.showsTopBar }
}

fun NavBackStackEntry.showBackButton(): Boolean {
    return destination.route?.startsWith(ScreensRoute.TaskDetails.route)
        ?: false
}
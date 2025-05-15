package com.example.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.navigation.presentation.view.MainScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.TaskDetailScreen

@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = ScreensRoute.Login.route) {
        composable(route = ScreensRoute.Login.route) {
            LoginScreen(navController)
        }
        composable(route = ScreensRoute.Home.route) {
            HomeScreen(navController, modifier)
        }
        composable(
            route = "${ScreensRoute.TaskDetails.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
//                    defaultValue = -1
//                    nullable = false
                }
            )
        ) { navBackStackEntry ->
            val taskID = navBackStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(
                modifier = modifier,
                taskId = taskID,
                onBackClick = { navController.popBackStack() },
                onViewNoteClick = {}
            )
        }
        composable(route = ScreensRoute.Favorites.route) {
//            FavoritesScreen()
            MainScreen(navController)
        }
        composable(route = ScreensRoute.Profile.route) {
//            ProfileScreen()
        }
    }
}

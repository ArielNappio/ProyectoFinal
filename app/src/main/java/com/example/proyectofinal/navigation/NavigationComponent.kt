package com.example.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.student.presentation.view.ChatScreen
import com.example.proyectofinal.student.presentation.view.CommentsScreen
import com.example.proyectofinal.student.presentation.view.FavoritesScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.StudentProfileScreen
import com.example.proyectofinal.student.presentation.view.TaskDetailScreen
import com.example.proyectofinal.task_student.presentation.view.TaskStudent

@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.Login.route
    ) {
        composable(route = ScreensRoute.Login.route) {
            LoginScreen(navController)
        }
        composable(route = ScreensRoute.Home.route) {
            HomeScreen(navController, modifier)
        }
        composable(
            route = "${ScreensRoute.TaskDetails.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val taskID = navBackStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(
                modifier = modifier,
                taskId = taskID,
                navController = navController
            )
        }
        composable(route = ScreensRoute.Task.route){
            TaskStudent(navController)
        }
        composable(route = ScreensRoute.Favorites.route) {
            FavoritesScreen(modifier, navController)
        }
        composable(route = ScreensRoute.Profile.route) {
            StudentProfileScreen(modifier)
        }
        composable(
            route = "comments/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) {
            val taskId = it.arguments?.getInt("taskId") ?: 0
            CommentsScreen(
                navController,
                modifier = modifier,
                taskId = taskId
            )
        }
        composable(route = ScreensRoute.Chat.route) {
            ChatScreen(modifier, navController)
        }
    }
}

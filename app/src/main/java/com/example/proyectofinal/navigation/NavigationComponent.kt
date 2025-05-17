package com.example.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.student.presentation.view.FavoriteScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.TaskDetailScreen
import com.example.proyectofinal.task_student.presentation.view.TaskStudent

@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
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
                onViewNoteClick = {},
                navController = navController
            )
        }
        composable(route = ScreensRoute.Task.route){
            TaskStudent(navController)
        }

        composable(route = ScreensRoute.Favorites.route) {
            FavoriteScreen(navController)
        }
        composable(route = ScreensRoute.Profile.route) {
//            MainScreen(navController)
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
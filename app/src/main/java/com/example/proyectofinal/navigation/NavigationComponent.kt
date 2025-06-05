package com.example.proyectofinal.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinal.mail.domain.model.MailboxType
//import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.mail.presentation.view.InboxScreen
import com.example.proyectofinal.mail.presentation.view.MessageScreen
import com.example.proyectofinal.student.presentation.view.CommentsScreen
import com.example.proyectofinal.student.presentation.view.FavoritesScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.StudentProfileScreen
import com.example.proyectofinal.student.presentation.view.TaskDetailScreen
import com.example.proyectofinal.task_student.presentation.view.TaskStudent
import com.example.proyectofinal.userpreferences.presentation.view.FontPreferencesScreen
import com.example.proyectofinal.text_editor.presentation.view.TextEditorScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationComponent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.Home.route
    ) {
//        composable(route = ScreensRoute.Login.route) {
//            LoginScreen(navController)
//        }
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
            StudentProfileScreen(modifier, navController)
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
        composable(ScreensRoute.Mail.route) { backStackEntry ->
            val mailboxType = when (backStackEntry.arguments?.getString("mailboxType")) {
                "inbox" -> MailboxType.INBOX
                "outbox" -> MailboxType.OUTBOX
                "drafts" -> MailboxType.DRAFT
                else -> MailboxType.INBOX
            }
            InboxScreen(
                navController = navController,
                mailboxType = mailboxType,
                onMessageClick = { }
            )
        }
        composable(
            route = "${ScreensRoute.Message.route}?draftId={draftId}",
            arguments = listOf(
                navArgument("draftId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val draftId = backStackEntry.arguments?.getInt("draftId") ?: -1

            MessageScreen(
                draftId = draftId,
                onCancel = { navController.popBackStack() },
                onDraftSaved = { navController.navigate("mail/drafts") },
                onSendComplete = { navController.navigate("mail/outbox") }
            )
        }
        composable(route = ScreensRoute.Preferences.route) {
            FontPreferencesScreen { navController.navigate(ScreensRoute.Home.route) }
        }

        composable(
            route = ScreensRoute.TextEdit.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) {
            val taskId = it.arguments?.getInt("taskId") ?: 0
            TextEditorScreen(navController, taskId)
        }
    }
}

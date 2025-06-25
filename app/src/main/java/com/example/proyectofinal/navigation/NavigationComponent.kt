package com.example.proyectofinal.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinal.auth.presentation.view.LoginScreen
import com.example.proyectofinal.mail.domain.model.MailboxType
import com.example.proyectofinal.mail.presentation.view.InboxScreen
import com.example.proyectofinal.mail.presentation.view.MessageDetailScreen
import com.example.proyectofinal.mail.presentation.view.MessageScreen
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import com.example.proyectofinal.student.presentation.view.CommentsScreen
import com.example.proyectofinal.student.presentation.view.FavoritesScreen
import com.example.proyectofinal.student.presentation.view.HomeScreen
import com.example.proyectofinal.student.presentation.view.ProjectDetailScreen
import com.example.proyectofinal.student.presentation.view.SearchScreen
import com.example.proyectofinal.student.presentation.view.StudentProfileScreen
import com.example.proyectofinal.student.presentation.view.TaskDetailScreen
import com.example.proyectofinal.task_student.presentation.view.TaskStudent
import com.example.proyectofinal.text_editor.presentation.view.TextEditorScreen
import com.example.proyectofinal.userpreferences.presentation.view.FontPreferencesScreen
import com.example.proyectofinal.users.presentation.view.CreateUserScreen
import com.example.proyectofinal.users.presentation.view.ManageUserScreen
import com.example.proyectofinal.users.presentation.view.UpdateUserScreen
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
        composable(route = ScreensRoute.Search.route) {
            SearchScreen(navController)
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
        composable(
            route = "${ScreensRoute.ProjectDetail.route}/{projectId}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val projectId = navBackStackEntry.arguments?.getString("projectId") ?: ""
            ProjectDetailScreen(
                projectId = projectId,
                navController = navController,
                modifier = modifier
            )
        }
        composable(
            route = "${ScreensRoute.Task.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val taskId = navBackStackEntry.arguments?.getInt("taskId") ?: -1
            TaskStudent(
                taskId = taskId,
                navController = navController
            )
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
                onMessageClick = { message ->
                    navController.navigate(
                        "${ScreensRoute.MessageDetail.route}/messageId=${message.id}&mailboxType=${mailboxType.name}"
                    )
                }
            )
        }
        composable(
            route = "${ScreensRoute.MessageDetail.route}/messageId={messageId}&mailboxType={mailboxType}",
            arguments = listOf(
                navArgument("messageId") { type = NavType.IntType },
                navArgument("mailboxType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getInt("messageId") ?: -1
            val mailboxType = MailboxType.valueOf(backStackEntry.arguments?.getString("mailboxType") ?: "INBOX")

            val inboxViewModel: InboxViewModel = koinViewModel()
            val messageVm: MessageViewModel = koinViewModel()
            val message = inboxViewModel.getMessageById(messageId)

            val userEmail = inboxViewModel.userEmail.collectAsState().value
            val recipientEmail = messageVm.to.collectAsState().value

            LaunchedEffect(message) {
                message?.let {
                    if (mailboxType == MailboxType.OUTBOX || mailboxType == MailboxType.DRAFT) {
                        messageVm.getEmailByUserId(it.userToId)
                    }
                }
            }

            message?.let {
                MessageDetailScreen(
                    message = it,
                    mailboxType = mailboxType,
                    userEmail = userEmail ?: "Desconocido",
                    recipientEmail = if (mailboxType == MailboxType.INBOX) userEmail ?: "Desconocido" else recipientEmail,
                    onBack = { navController.popBackStack() },
                    onReply = { msg ->
                        navController.navigate(
                            "${ScreensRoute.Message.route}?draftId=-1&replyToSubject=${Uri.encode(msg.subject)}&fromUserId=${msg.userFromId}"
                        )
                    }
                )
            }
        }

        composable(
            route = "${ScreensRoute.Message.route}?draftId={draftId}&replyToSubject={replyToSubject}&fromUserId={fromUserId}",
            arguments = listOf(
                navArgument("draftId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("replyToSubject") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("fromUserId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val draftId = backStackEntry.arguments?.getInt("draftId") ?: -1
            val replyToSubject = backStackEntry.arguments?.getString("replyToSubject") ?: ""
            val fromUserId = backStackEntry.arguments?.getString("fromUserId") ?: ""

            MessageScreen(
                draftId = draftId,
                replyToSubject = replyToSubject.takeIf { it.isNotBlank() },
                fromUserId = fromUserId.takeIf { it.isNotBlank() },
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

        composable(
            route = "${ScreensRoute.UpdateUser.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: 0
            UpdateUserScreen(
                userId = userId.toString(),
                navController = navController
            )
        }

        composable(route = ScreensRoute.ManageUsers.route) {
            ManageUserScreen(navController = navController)
        }

        composable(route = ScreensRoute.CreateUser.route) {
            CreateUserScreen(navController = navController)
        }

    }
}

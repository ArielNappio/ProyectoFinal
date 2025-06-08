package com.example.proyectofinal.navigation

enum class ScreensRoute(
    val route: String,
    val showsTopBar: Boolean = true,
    val showsBottomBar: Boolean = true
) {
    Login("login", false, false),
    Home("home"),
    Favorites("favorites"),
    Profile("profile"),
    TaskDetails("task_details", showsBottomBar = false),
    Task("task", false, false),
    Comments("comments", showsTopBar = false, showsBottomBar = false),
    Mail("mail/{mailboxType}", false, false),
    Message("message", false, false),
    Preferences("user_preferences", false, false),
    TextEdit("text_edit/{taskId}", showsTopBar = false, showsBottomBar = false)
}

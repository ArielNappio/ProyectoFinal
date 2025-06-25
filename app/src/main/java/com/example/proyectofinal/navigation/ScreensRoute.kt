package com.example.proyectofinal.navigation

enum class ScreensRoute(
    val route: String,
    val showsTopBar: Boolean = true,
    val showsBottomBar: Boolean = true,
    val showsBackButton: Boolean = false
) {
    Login("login", false, false),
    Home("home", showsTopBar = true, showsBottomBar = true),
    Search("search", false, false),
    Favorites("favorites"),
    Profile("profile"),
    ProjectDetail("project_detail", showsTopBar = false, showsBottomBar = false),
    TaskDetails("task_details", showsBottomBar = false, showsBackButton = true),
    Task("task", false, false),
    Comments("comments", showsTopBar = false, showsBottomBar = false),
    Mail("mail/{mailboxType}", false, showsBottomBar = false, showsBackButton = false),
    Message("message", false, false),
    MessageDetail("mail_detail", false, false),
    Preferences("user_preferences", false, false),
    TextEdit("text_edit/{taskId}", showsTopBar = false, showsBottomBar = false),
    UpdateUser("update_user", showsTopBar = true, showsBottomBar = false),
    ManageUsers("manage_users"),
    CreateUser("create_user")
}

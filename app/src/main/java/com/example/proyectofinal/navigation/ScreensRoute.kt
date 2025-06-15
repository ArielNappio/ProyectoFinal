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
    Chat("chat"),
    UpdateUser("update_user", showsTopBar = true, showsBottomBar = false),
    ManageUsers("manage_users")

}

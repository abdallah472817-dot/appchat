package com.example.chatapp.navigation

/** Destinations used by [AppNavGraph]. */
sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Chat : AppRoute("chat")
    data object Profile : AppRoute("profile")
    data object Settings : AppRoute("settings")
}

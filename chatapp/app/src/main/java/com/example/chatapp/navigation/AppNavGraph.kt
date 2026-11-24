package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.data.AuthRepository
import com.example.chatapp.data.ChatRepository
import com.example.chatapp.data.SettingsStore
import com.example.chatapp.ui.screens.AuthViewModel
import com.example.chatapp.ui.screens.ChatScreen
import com.example.chatapp.ui.screens.ChatViewModel
import com.example.chatapp.ui.screens.LoginScreen
import com.example.chatapp.ui.screens.ProfileScreen
import com.example.chatapp.ui.screens.SettingsScreen
import com.example.chatapp.ui.screens.SettingsViewModel
import com.example.chatapp.ui.theme.NeoBrutalistTheme

private const val DEFAULT_CHAT_ID = "general"

/** Root composable: theme follows the persisted dark-mode setting. */
@Composable
fun ChatApp(
    authRepository: AuthRepository,
    chatRepository: ChatRepository,
    settingsStore: SettingsStore,
) {
    val settingsViewModel: SettingsViewModel = viewModel { SettingsViewModel(settingsStore) }
    val settingsState by settingsViewModel.settings.collectAsState()
    NeoBrutalistTheme(darkTheme = settingsState.getOrNull()?.isDarkTheme ?: false) {
        AppNavGraph(authRepository, chatRepository, settingsViewModel)
    }
}

@Composable
fun AppNavGraph(
    authRepository: AuthRepository,
    chatRepository: ChatRepository,
    settingsViewModel: SettingsViewModel,
) {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }
    val currentUserState by authViewModel.currentUser.collectAsState()
    val currentUser = currentUserState.getOrNull()

    NavHost(navController = navController, startDestination = AppRoute.Login.route) {
        composable(AppRoute.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onSignedIn = {
                    navController.navigate(AppRoute.Chat.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoute.Chat.route) {
            val chatViewModel: ChatViewModel = viewModel {
                ChatViewModel(chatRepository, DEFAULT_CHAT_ID)
            }
            ChatScreen(
                chatViewModel = chatViewModel,
                currentUser = currentUser,
                onOpenProfile = { navController.navigate(AppRoute.Profile.route) },
                onOpenSettings = { navController.navigate(AppRoute.Settings.route) },
            )
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onSignedOut = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Chat.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

package com.example.chatapp.model

/**
 * Immutable snapshot of the preferences persisted in Preferences DataStore.
 */
data class AppSettings(
    val isDarkTheme: Boolean = false,
    val areNotificationsEnabled: Boolean = true,
    val language: String = "en",
)

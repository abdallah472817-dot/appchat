package com.example.chatapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatapp.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATASTORE_NAME = "app_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME,
)

/**
 * Single source of truth for user preferences. The UI only observes [settings]
 * and calls the setters; Firebase/DataStore APIs never leak into composables.
 */
class SettingsStore(private val context: Context) {

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

        private const val DEFAULT_DARK_MODE = false
        private const val DEFAULT_NOTIFICATIONS_ENABLED = true
    }

    val settings: Flow<Result<AppSettings>> = context.settingsDataStore.data
        .map { preferences ->
            try {
                Result.success(
                    AppSettings(
                        isDarkTheme = preferences[IS_DARK_MODE] ?: DEFAULT_DARK_MODE,
                        areNotificationsEnabled = preferences[NOTIFICATIONS_ENABLED]
                            ?: DEFAULT_NOTIFICATIONS_ENABLED,
                    ),
                )
            } catch (error: Exception) {
                Result.failure(error)
            }
        }
        // DataStore read failures arrive here as Result.failure too.
        .catch { error -> emit(Result.failure(error)) }

    suspend fun setIsDarkMode(enabled: Boolean): Result<Unit> = try {
        context.settingsDataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    suspend fun setNotificationsEnabled(enabled: Boolean): Result<Unit> = try {
        context.settingsDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }
}

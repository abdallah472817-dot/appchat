package com.example.chatapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.SettingsStore
import com.example.chatapp.model.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Exposes DataStore as state; composables only read/emit intents. */
class SettingsViewModel(private val store: SettingsStore) : ViewModel() {

    val settings: StateFlow<Result<AppSettings>> = store.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Result.success(AppSettings()))

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { store.setIsDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { store.setNotificationsEnabled(enabled) }
    }
}

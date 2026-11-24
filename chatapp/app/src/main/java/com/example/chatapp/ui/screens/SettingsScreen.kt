package com.example.chatapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.components.NeoBrutalistCard
import com.example.chatapp.ui.components.NeoBrutalistCardStyle
import com.example.chatapp.ui.theme.NeoBrutalistBorderWidth
import com.example.chatapp.ui.theme.NeoBrutalistCornerShape

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingsState by settingsViewModel.settings.collectAsState()
    val settings = settingsState.getOrNull()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            NeoBrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                style = NeoBrutalistCardStyle.Rose,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Dark mode",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Switch(
                        checked = settings?.isDarkTheme ?: false,
                        onCheckedChange = settingsViewModel::setDarkMode,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeoBrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                style = NeoBrutalistCardStyle.Paper,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Switch(
                        checked = settings?.areNotificationsEnabled ?: true,
                        onCheckedChange = settingsViewModel::setNotificationsEnabled,
                    )
                }
            }

            settingsState.exceptionOrNull()?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ERROR: ${it.message}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBack,
                shape = NeoBrutalistCornerShape,
                border = androidx.compose.foundation.BorderStroke(
                    NeoBrutalistBorderWidth,
                    MaterialTheme.colorScheme.outline,
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(text = "BACK", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

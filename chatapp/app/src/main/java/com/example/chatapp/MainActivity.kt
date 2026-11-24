package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatapp.data.AuthRepository
import com.example.chatapp.data.ChatRepository
import com.example.chatapp.data.SettingsStore
import com.example.chatapp.navigation.ChatApp
import com.example.chatapp.ui.theme.NeoBrutalistTheme

/**
 * Host activity: builds the data-layer dependencies and hands them to the
 * navigation graph. Google web client ID is resolved at runtime so the app
 * still compiles before a real google-services.json is installed.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleWebClientId = resolveGoogleWebClientId()
        if (googleWebClientId.isNullOrBlank()) {
            showConfigurationError(
                "Google web client ID is missing. Add the real google-services.json " +
                    "(with an OAuth web client) to app/ and rebuild.",
            )
            return
        }

        val authRepository = try {
            AuthRepository(context = this, googleWebClientId = googleWebClientId)
        } catch (error: Exception) {
            showConfigurationError("Could not initialize authentication: ${error.message}")
            return
        }

        val chatRepository = try {
            ChatRepository()
        } catch (error: Exception) {
            showConfigurationError("Could not initialize chat: ${error.message}")
            return
        }

        val settingsStore = SettingsStore(applicationContext)

        setContent {
            ChatApp(
                authRepository = authRepository,
                chatRepository = chatRepository,
                settingsStore = settingsStore,
            )
        }
    }

    /** Returns null when the plugin has not generated the resource yet. */
    private fun resolveGoogleWebClientId(): String? {
        val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (resourceId == 0) return null
        return try {
            getString(resourceId)
        } catch (error: Exception) {
            null
        }
    }

    private fun showConfigurationError(message: String) {
        setContent {
            NeoBrutalistTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "SETUP REQUIRED",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}

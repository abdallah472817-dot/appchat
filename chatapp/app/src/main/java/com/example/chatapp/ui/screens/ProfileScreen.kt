package com.example.chatapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.components.NeoBrutalistCard
import com.example.chatapp.ui.components.NeoBrutalistCardStyle
import com.example.chatapp.ui.theme.NeoBrutalistBorderWidth
import com.example.chatapp.ui.theme.NeoBrutalistCornerShape

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSignedOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userState by authViewModel.currentUser.collectAsState()
    val signOutState by authViewModel.signOutState.collectAsState()
    val user = userState.getOrNull()

    LaunchedEffect(signOutState) {
        if (signOutState?.isSuccess == true) {
            authViewModel.consumeSignOutResult()
            onSignedOut()
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            NeoBrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                style = NeoBrutalistCardStyle.Paper,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = user?.displayName?.ifBlank { "User" } ?: "Not signed in",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user?.email?.ifBlank { "No email" } ?: "No email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            userState.exceptionOrNull()?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ERROR: ${it.message}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            signOutState?.exceptionOrNull()?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ERROR: ${it.message}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onBack,
                    shape = NeoBrutalistCornerShape,
                    border = BorderStroke(NeoBrutalistBorderWidth, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Text(text = "BACK", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.width(0.dp))
                Button(
                    onClick = { authViewModel.signOut() },
                    shape = NeoBrutalistCornerShape,
                    border = BorderStroke(NeoBrutalistBorderWidth, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(text = "SIGN OUT", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

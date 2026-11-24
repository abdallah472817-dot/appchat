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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatapp.model.UserProfile
import com.example.chatapp.ui.components.NeoBrutalistCard
import com.example.chatapp.ui.components.NeoBrutalistCardStyle
import com.example.chatapp.ui.theme.NeoBrutalistBorderWidth
import com.example.chatapp.ui.theme.NeoBrutalistCornerShape

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    currentUser: UserProfile?,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val messagesState by chatViewModel.messages.collectAsState()
    val input by chatViewModel.input.collectAsState()
    val error by chatViewModel.error.collectAsState()
    val isSending by chatViewModel.isSending.collectAsState()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                NeoBrutalistTextButton(text = "PROFILE", onClick = onOpenProfile)
                NeoBrutalistTextButton(text = "SETTINGS", onClick = onOpenSettings)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val messages = messagesState.getOrNull().orEmpty()
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(messages, key = { it.id }) { message ->
                    val isMine = message.senderId == currentUser?.uid
                    NeoBrutalistCard(
                        modifier = Modifier.fillMaxWidth(),
                        style = if (isMine) NeoBrutalistCardStyle.Rose else NeoBrutalistCardStyle.Paper,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = message.senderName.ifBlank { "Unknown" },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isMine) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                }
            }

            messagesState.exceptionOrNull()?.let {
                Text(
                    text = "ERROR: ${it.message}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            error?.let {
                Text(
                    text = "ERROR: $it",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = chatViewModel::onInputChanged,
                    modifier = Modifier.weight(1f),
                    label = { Text("Message") },
                    shape = NeoBrutalistCornerShape,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { chatViewModel.sendMessage(currentUser) },
                    enabled = !isSending,
                    shape = NeoBrutalistCornerShape,
                    border = BorderStroke(NeoBrutalistBorderWidth, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(text = "SEND", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun NeoBrutalistTextButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = NeoBrutalistCornerShape,
        border = BorderStroke(NeoBrutalistBorderWidth, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

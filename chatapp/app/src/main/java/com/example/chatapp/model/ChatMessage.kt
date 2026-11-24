package com.example.chatapp.model

/**
 * Firestore-backed chat message document.
 * [timestamp] is stored as epoch millis (UTC); the UI formats it for display.
 */
data class ChatMessage(
    var id: String = "",
    var chatId: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var text: String = "",
    var timestamp: Long = 0L,
)

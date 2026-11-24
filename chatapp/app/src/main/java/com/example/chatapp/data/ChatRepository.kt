package com.example.chatapp.data

import com.example.chatapp.model.ChatMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Converts a Firestore Task into a suspend call without exposing Tasks upward. */
private suspend fun <T> Task<T>.awaitTask(action: String): T? = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(
                task.exception ?: Exception("$action failed with an unknown error"),
            )
        }
    }
}

/**
 * Firestore access for messages: chats/{chatId}/messages/{messageId}.
 * Snapshot listener errors and mapping errors are delivered as Result.failure.
 */
class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun messages(chatId: String) = firestore
        .collection("chats")
        .document(chatId)
        .collection("messages")

    fun observeMessages(chatId: String): Flow<Result<List<ChatMessage>>> = callbackFlow {
        if (chatId.isBlank()) {
            trySend(Result.failure(Exception("Chat id must not be blank")))
            close()
            return@callbackFlow
        }
        val registration = messages(chatId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(
                    Result.failure(
                        Exception("Could not listen to messages in chat '$chatId': ${error.message}", error),
                    ),
                )
                return@addSnapshotListener
            }
            try {
                val messages = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(ChatMessage::class.java)?.copy(id = document.id)
                }.orEmpty().sortedBy { it.timestamp }
                trySend(Result.success(messages))
            } catch (mappingError: Exception) {
                trySend(
                    Result.failure(
                        Exception(
                            "Could not read messages in chat '$chatId': ${mappingError.message}",
                            mappingError,
                        ),
                    ),
                )
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun sendMessage(
        chatId: String,
        text: String,
        senderId: String,
        senderName: String,
    ): Result<ChatMessage> {
        if (chatId.isBlank()) return Result.failure(Exception("Chat id must not be blank"))
        if (senderId.isBlank()) return Result.failure(Exception("Sender id must not be blank"))
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return Result.failure(Exception("Message text must not be blank"))
        return try {
            val reference = messages(chatId).document()
            val message = ChatMessage(
                id = reference.id,
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = trimmedText,
                timestamp = System.currentTimeMillis(),
            )
            reference.set(message).awaitTask("Sending message")
            Result.success(message)
        } catch (error: Exception) {
            Result.failure(Exception("Could not send message in chat '$chatId': ${error.message}", error))
        }
    }
}

package com.example.chatapp.model

/**
 * Firestore-backed user document.
 * Default values give Firebase a no-arg constructor for deserialization.
 */
data class UserProfile(
    var uid: String = "",
    var displayName: String = "",
    var email: String = "",
    var photoUrl: String = "",
    var createdAt: Long = 0L,
)

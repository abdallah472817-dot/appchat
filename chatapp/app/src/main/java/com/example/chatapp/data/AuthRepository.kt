package com.example.chatapp.data

import android.content.Context
import android.content.Intent
import com.example.chatapp.model.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Bridges Google Play Tasks into coroutines without leaking Tasks to callers. */
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
 * All Firebase Auth / Firestore access for account data lives here.
 * The UI passes in the Google sign-in Intent result; no composable API is used.
 */
class AuthRepository(
    context: Context,
    private val googleWebClientId: String,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val appContext = context.applicationContext
    private val users get() = firestore.collection("users")

    private val googleSignInClient: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(appContext, options)
    }

    /** Launch this from the UI with ActivityResultContracts.StartActivityForResult(). */
    fun googleSignInIntent(): Intent = googleSignInClient.signInIntent

    /** Emits the signed-in profile, or null after sign-out. */
    fun observeCurrentUser(): Flow<Result<UserProfile?>> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                trySend(Result.success(null))
            } else {
                launch { trySend(getUserProfile(uid)) }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** Completes Google sign-in from the Intent returned by the sign-in activity. */
    suspend fun completeGoogleSignIn(data: Intent?): Result<UserProfile> {
        if (data == null) {
            return Result.failure(Exception("Google Sign-In was canceled or returned no data"))
        }
        return try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .awaitTask("Reading the Google account")
                ?: throw Exception("Google account was empty")
            val idToken = account.idToken
                ?: throw Exception("Google account provided no ID token; check the web client ID")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).awaitTask("Firebase sign-in")
                ?: throw Exception("Firebase sign-in returned no result")
            val user = authResult.user ?: throw Exception("Firebase sign-in returned no user")
            saveUserProfile(
                user = user,
                displayName = account.displayName,
                photoUrl = account.photoUrl?.toString(),
            )
        } catch (error: ApiException) {
            Result.failure(
                Exception("Google Sign-In failed (code ${error.statusCode}): ${error.message}", error),
            )
        } catch (error: Exception) {
            Result.failure(Exception("Google Sign-In failed: ${error.message}", error))
        }
    }

    suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        googleSignInClient.signOut().awaitTask("Google sign-out")
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(Exception("Sign-out failed: ${error.message}", error))
    }

    suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        if (uid.isBlank()) return Result.failure(Exception("User id must not be blank"))
        return try {
            val snapshot = users.document(uid).get().awaitTask("Loading user profile '$uid'")
            Result.success(snapshot?.toObject(UserProfile::class.java)?.copy(uid = uid))
        } catch (error: Exception) {
            Result.failure(Exception("Could not load user profile '$uid': ${error.message}", error))
        }
    }

    /** Creates the user document on first sign-in and keeps its original createdAt. */
    suspend fun saveUserProfile(
        user: FirebaseUser,
        displayName: String? = null,
        photoUrl: String? = null,
    ): Result<UserProfile> = try {
        val reference = users.document(user.uid)
        val existing = reference.get().awaitTask("Loading user profile '${user.uid}'")
            ?.toObject(UserProfile::class.java)
        val profile = UserProfile(
            uid = user.uid,
            displayName = displayName?.takeIf { it.isNotBlank() }
                ?: user.email?.substringBefore('@')?.takeIf { it.isNotBlank() }
                ?: "User",
            email = user.email ?: "",
            photoUrl = photoUrl ?: "",
            createdAt = existing?.createdAt?.takeIf { it > 0 } ?: System.currentTimeMillis(),
        )
        reference.set(profile).awaitTask("Saving user profile '${user.uid}'")
        Result.success(profile)
    } catch (error: Exception) {
        Result.failure(Exception("Could not save user profile '${user.uid}': ${error.message}", error))
    }
}

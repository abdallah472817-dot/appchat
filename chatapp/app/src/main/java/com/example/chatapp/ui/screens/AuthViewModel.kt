package com.example.chatapp.ui.screens

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.AuthRepository
import com.example.chatapp.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val error: String? = null,
)

/** Holds auth state; the repository is the only place that touches Firebase/Google. */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val currentUser: StateFlow<Result<UserProfile?>> = repository.observeCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Result.success(null))

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _signOutState = MutableStateFlow<Result<Unit>?>(null)
    val signOutState: StateFlow<Result<Unit>?> = _signOutState.asStateFlow()

    /** The screen launches this Intent with ActivityResultContracts. */
    fun signInIntent(): Intent = repository.googleSignInIntent()

    fun onGoogleSignInResult(data: Intent?, isOk: Boolean) {
        if (!isOk) {
            _loginState.value = LoginUiState(error = "Google Sign-In was canceled")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginUiState(isLoading = true)
            val result = repository.completeGoogleSignIn(data)
            _loginState.value = if (result.isSuccess) {
                LoginUiState(isSignedIn = true)
            } else {
                LoginUiState(error = result.exceptionOrNull()?.message ?: "Sign-in failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _signOutState.value = repository.signOut()
        }
    }

    fun consumeSignOutResult() {
        _signOutState.value = null
    }
}

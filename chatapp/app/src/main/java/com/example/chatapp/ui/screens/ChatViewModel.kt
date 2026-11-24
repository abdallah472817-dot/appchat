package com.example.chatapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.ChatRepository
import com.example.chatapp.model.ChatMessage
import com.example.chatapp.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    private val chatId: String,
) : ViewModel() {

    val messages: StateFlow<Result<List<ChatMessage>>> = repository.observeMessages(chatId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Result.success(emptyList()))

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun onInputChanged(value: String) {
        _input.value = value
    }

    fun sendMessage(sender: UserProfile?) {
        val text = _input.value.trim()
        if (text.isEmpty()) {
            _error.value = "Type a message first"
            return
        }
        if (sender == null) {
            _error.value = "Sign in before sending a message"
            return
        }
        _isSending.value = true
        viewModelScope.launch {
            try {
                val result = repository.sendMessage(chatId, text, sender.uid, sender.displayName)
                if (result.isSuccess) {
                    _input.value = ""
                    _error.value = null
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Could not send message"
                }
            } finally {
                _isSending.value = false
            }
        }
    }
}

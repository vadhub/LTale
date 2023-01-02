package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.repository.MessageRepository

class MessageViewModelFactory(private val messageRepository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageViewModel(messageRepository) as T
    }
}
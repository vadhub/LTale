package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.Post
import com.vad.ltale.data.repository.MessageRepository
import kotlinx.coroutines.launch

class MessageViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    var messages: MutableLiveData<List<Post>> = MutableLiveData()

    fun getMessages() = viewModelScope.launch {
        messages.postValue(messageRepository.getMessages())
    }

    fun getMessageById(id: Int) = viewModelScope.launch {
        messages.postValue(messageRepository.getMessageById(id))
    }

    fun getMessageByUserId(userId: Int) = viewModelScope.launch {
        messages.postValue(messageRepository.getMessageByUserId(userId))
    }
}
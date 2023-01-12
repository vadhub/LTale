package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.PostRepository
import kotlinx.coroutines.launch

class MessageViewModel(private val postRepository: PostRepository) : ViewModel() {
    var messages: MutableLiveData<List<PostResponse>> = MutableLiveData()

    fun getMessages() = viewModelScope.launch {
        messages.postValue(postRepository.getMessages())
    }

    fun getMessageById(id: Int) = viewModelScope.launch {
        messages.postValue(postRepository.getMessageById(id))
    }

    fun getMessageByUserId(userId: Int) = viewModelScope.launch {
        messages.postValue(postRepository.getMessageByUserId(userId))
    }
}
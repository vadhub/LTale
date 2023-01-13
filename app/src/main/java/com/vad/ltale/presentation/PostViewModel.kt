package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.PostRequest
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()

    fun getPosts() = viewModelScope.launch {
        posts.postValue(postRepository.getPosts())
    }

    fun getPostById(id: Int) = viewModelScope.launch {
        posts.postValue(postRepository.getPostById(id))
    }

    fun getPostByUserId(userId: Int) = viewModelScope.launch {
        posts.postValue(postRepository.getPostByUserId(userId))
    }

    fun postPost(postRequest: PostRequest) = viewModelScope.launch {
        postRepository.postPost(postRequest)
    }
}
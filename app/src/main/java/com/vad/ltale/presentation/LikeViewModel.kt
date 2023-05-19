package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.Like
import com.vad.ltale.data.repository.LikeRepository
import kotlinx.coroutines.launch

class LikeViewModel(private val likeRepository: LikeRepository) : ViewModel() {

    fun addLike(like: Like) = viewModelScope.launch {
        likeRepository.addLike(like)
    }

    fun deleteLike(like: Like) = viewModelScope.launch {
        likeRepository.deleteLike(like)
    }

}
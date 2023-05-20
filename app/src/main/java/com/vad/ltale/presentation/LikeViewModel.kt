package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.Like
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.LikeRepository
import kotlinx.coroutines.launch

class LikeViewModel(private val likeRepository: LikeRepository) : ViewModel() {

    var likeData: MutableLiveData<Pair<Int, PostResponse>> = MutableLiveData()

    fun addLike(like: Like, position: Int, post: PostResponse) = viewModelScope.launch {
        post.isLiked = true
        post.countLike++
        likeRepository.addLike(like)
        likeData.postValue(Pair(position, post))
    }

    fun deleteLike(like: Like, position: Int, post: PostResponse) = viewModelScope.launch {
        post.isLiked = false
        post.countLike--
        likeRepository.deleteLike(like)
        likeData.postValue(Pair(position, post))
    }

}
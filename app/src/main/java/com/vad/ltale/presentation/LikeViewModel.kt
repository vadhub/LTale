package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.pojo.Like
import com.vad.ltale.model.pojo.PostResponse
import com.vad.ltale.data.repository.LikeRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikeViewModel(private val likeRepository: LikeRepository) : ViewModel() {

    var likeData: MutableLiveData<Pair<Int, PostResponse>> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun addLike(like: Like, position: Int, post: PostResponse) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        post.isLiked = true
        post.countLike++
        likeRepository.addLike(like)
        likeData.postValue(Pair(position, post))
    }

    fun deleteLike(like: Like, position: Int, post: PostResponse) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        post.isLiked = false
        post.countLike--
        likeRepository.deleteLike(like)
        likeData.postValue(Pair(position, post))
    }

}

@Suppress("UNCHECKED_CAST")
class LikeViewModelFactory(private val repository: LikeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LikeViewModel(repository) as T
    }
}
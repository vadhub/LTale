package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.model.pojo.Follow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowViewModel(private val followRepository: FollowRepository): ViewModel() {

    var countOfSubscribers: MutableLiveData<Long> = MutableLiveData()
    var isSubscribe: MutableLiveData<Boolean> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun getSubscribers(userId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        countOfSubscribers.postValue(followRepository.getSubscribers(userId))
    }

    fun subscribe(countFollowers: Long, follow: Follow) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        var countFollower = countFollowers
        followRepository.subscribe(follow)
        countOfSubscribers.postValue(++countFollower)
    }

    fun checkSubscribe(follower: Long, followed: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        isSubscribe.postValue(followRepository.isSubscribe(follower, followed).equals("true"))
    }

    fun unsubscribe(countFollowers: Long, follow: Follow) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        var countFollower = countFollowers
        followRepository.unsubscribe(follow)
        countOfSubscribers.postValue(--countFollower)
    }
}

class FollowViewModelFactory(private val followRepository: FollowRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowViewModel(followRepository) as T
    }
}
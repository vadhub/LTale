package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.repository.FollowRepository
import com.vad.ltale.model.Follow
import kotlinx.coroutines.launch

class FollowViewModel(private val followRepository: FollowRepository): ViewModel() {

    var mutableLiveData: MutableLiveData<Int> = MutableLiveData()

    fun subscribe(countFollowers: Int, follow: Follow) = viewModelScope.launch {
        var countFollower = countFollowers
        followRepository.subscribe(follow)
        mutableLiveData.postValue(++countFollower)
    }

    fun unsubscribe(countFollowers: Int, follow: Follow) = viewModelScope.launch {
        var countFollower = countFollowers
        followRepository.unsubscribe(follow)
        mutableLiveData.postValue(--countFollower)
    }
}

class FollowViewModelFactory(private val followRepository: FollowRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowViewModel(followRepository) as T
    }
}
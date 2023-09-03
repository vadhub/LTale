package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.Limit
import com.vad.ltale.data.repository.LimitRepository
import kotlinx.coroutines.launch

class LimitViewModel(private val repository: LimitRepository) : ViewModel() {

    val limit: MutableLiveData<Limit> = MutableLiveData()

    fun updateTime(limit: Limit) = viewModelScope.launch {
        repository.update(limit.id, limit)
    }

    fun getLimit(userId: Long) = viewModelScope.launch {
        limit.postValue(repository.getByUserId(userId))
    }

}

@Suppress("UNCHECKED_CAST")
class LimitViewModelFactory(private val repository: LimitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LimitViewModel(repository) as T
    }
}
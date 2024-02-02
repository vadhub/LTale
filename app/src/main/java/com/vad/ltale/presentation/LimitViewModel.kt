package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.model.pojo.Limit
import com.vad.ltale.data.repository.LimitRepository
import ir.logicbase.livex.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LimitViewModel(private val repository: LimitRepository) : ViewModel() {

    val limit: MutableLiveData<Limit> = MutableLiveData()
    val faller: SingleLiveEvent<Exception> = SingleLiveEvent()
    var time = 0L

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun updateTime(limit: Limit) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        val response = repository.update(limit.id, limit)

        if (response is Resource.Failure) {
            faller.postValue(response.exception)
        }
    }

    fun getLimit(userId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        val response = repository.getByUserId(userId)

        if (response is Resource.Failure) {
            faller.postValue(response.exception)
        } else {
            limit.postValue((response as Resource.Success).result!!)
        }
    }

}

@Suppress("UNCHECKED_CAST")
class LimitViewModelFactory(private val repository: LimitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LimitViewModel(repository) as T
    }
}
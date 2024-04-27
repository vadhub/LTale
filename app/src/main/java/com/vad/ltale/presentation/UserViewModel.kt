package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.pojo.User
import com.vad.ltale.data.repository.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    var userDetails: MutableLiveData<User> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun getUser(id: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        userDetails.postValue(userRepository.getUserById(id))
    }

    fun changeUsername(newNik: String, id: Long) = viewModelScope.launch {
        userRepository.changeUsername(newNik, id)
    }

}

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepository) as T
    }
}
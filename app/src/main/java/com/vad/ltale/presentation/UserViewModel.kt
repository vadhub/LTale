package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.pojo.User
import com.vad.ltale.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    var userDetails: MutableLiveData<User> = MutableLiveData()

    fun getUser(id: Long) = viewModelScope.launch {
        userDetails.postValue(userRepository.getUserById(id))
    }

}

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepository) as T
    }
}
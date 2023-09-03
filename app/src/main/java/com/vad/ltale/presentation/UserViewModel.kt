package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.User
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository, private val handleResponse: HandleResponse) : ViewModel() {

    var userDetails: MutableLiveData<User> = MutableLiveData()

    fun getUser(id: Long) = viewModelScope.launch {
        userDetails.postValue(userRepository.getUserById(id))
    }

    fun getUserByUsername(username: String) = viewModelScope.launch {
        try {
            userDetails.postValue(userRepository.login(username))
            handleResponse.success()
        } catch (e: java.lang.Exception) {
            handleResponse.error()
        }

    }

    fun createUser(user: User) = viewModelScope.launch {
        try {
            userDetails.postValue(userRepository.creteUser(user))
            handleResponse.success()
        } catch (e: Exception) {
            handleResponse.error()
        }

    }
}

class UserViewModelFactory(private val userRepository: UserRepository, private val handleResponse: HandleResponse) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepository, handleResponse) as T
    }
}
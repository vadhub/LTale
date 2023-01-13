package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.User
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.HandleResponse
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository, private val handleResponse: HandleResponse) : ViewModel() {
    var users: MutableLiveData<List<User>> = MutableLiveData()
    var userDetails: MutableLiveData<User> = MutableLiveData()

    fun getUsers() = viewModelScope.launch {
        users.postValue(userRepository.getUsers())
    }

    fun getUser(id: Int) = viewModelScope.launch {
        users.postValue(userRepository.getUserById(id))
    }

    fun getUserByUsername(username: String) = viewModelScope.launch {
        try {
            userDetails.postValue(userRepository.login(username))
        } catch (e: java.lang.Exception) {
            handleResponse.error()
        }

    }

    fun createUser(user: User) = viewModelScope.launch {
        userRepository.creteUser(user)
    }
}
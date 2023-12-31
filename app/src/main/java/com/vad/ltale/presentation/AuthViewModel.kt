package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModel() {

    fun login(username: String) = viewModelScope.launch {
        try {
            handleResponse.success(userRepository.login(username))
        } catch (e: Exception) {
            e.message?.let { handleResponse.error(it) }
        }

    }

    fun createUser(user: User) = viewModelScope.launch {
        try {
            handleResponse.success(userRepository.createUser(user))
        } catch (e: Exception) {
            e.message?.let { handleResponse.error(it) }
        }

    }
}

class AuthViewModelFactory(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(userRepository, handleResponse) as T
    }
}
package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.remote.exception.UserNotFoundException
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModel() {

    fun login(username: String) = viewModelScope.launch {
            val response = userRepository.login(username)

            if (response is Resource.Failure) {
                handleResponse.error(response.exception)
            } else {
                handleResponse.success((response as Resource.Success).result)
            }

    }

    fun createUser(user: User) = viewModelScope.launch {
        val response = userRepository.createUser(user)

        if (response is Resource.Failure) {
            handleResponse.error(response.exception)
        } else {
            handleResponse.success((response as Resource.Success).result)
        }

    }
}

class AuthViewModelFactory(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(userRepository, handleResponse) as T
    }
}
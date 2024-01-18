package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.pojo.User
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModel() {

    fun login(username: String) = viewModelScope.launch {
            handleResponse(userRepository.login(username))
    }

    fun register(user: User) = viewModelScope.launch {
        handleResponse(userRepository.createUser(user))
    }

    private fun handleResponse(response: Resource<User>) {
        if (response is Resource.Failure) {
            handleResponse.error(response.exception)
        } else {
            handleResponse.success((response as Resource.Success).result)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val userRepository: UserRepository, private val handleResponse: HandleResponse<User>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(userRepository, handleResponse) as T
    }
}
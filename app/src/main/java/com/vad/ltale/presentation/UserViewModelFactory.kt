package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.HandleResponse

class UserViewModelFactory(private val userRepository: UserRepository, private val handleResponse: HandleResponse) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepository, handleResponse) as T
    }
}
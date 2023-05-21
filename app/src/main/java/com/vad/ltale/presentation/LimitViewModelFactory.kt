package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.repository.LimitRepository

@Suppress("UNCHECKED_CAST")
class LimitViewModelFactory(private val repository: LimitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LimitViewModel(repository) as T
    }
}
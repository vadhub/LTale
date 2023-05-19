package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.repository.LikeRepository

class LikeViewModelFactory(private val repository: LikeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LikeViewModel(repository) as T
    }
}
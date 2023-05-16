package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.repository.FileRepository

class LoadViewModelFactory(private val repository: FileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(repository) as T
    }
}
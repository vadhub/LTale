package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.remote.RemoteInstance

class LoadViewModelFactory(private val retrofitInstance: RemoteInstance): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(retrofitInstance) as T
    }
}
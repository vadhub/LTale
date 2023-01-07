package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance

class LoadViewModelFactory(private val retrofitInstance: RetrofitInstance): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileViewModel(retrofitInstance) as T
    }
}
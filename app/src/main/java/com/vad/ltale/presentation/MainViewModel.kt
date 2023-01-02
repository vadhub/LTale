package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var userId: Int = 0

    fun getUserId() = userId
    fun setUserId(id: Int) {
        this.userId= id;
    }
}
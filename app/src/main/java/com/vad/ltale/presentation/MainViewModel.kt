package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance

class MainViewModel : ViewModel() {

    private var userId: Int = 0
    private var retrofitInstance: RetrofitInstance = RetrofitInstance(UserDetails("",""))

    fun getRetrofit() = retrofitInstance
    fun setRetrofit(retrofitInstance: RetrofitInstance) {
        this.retrofitInstance = retrofitInstance
    }

    fun getUserId() = userId
    fun setUserId(id: Int) {
        this.userId= id;
    }
}
package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance

class MainViewModel : ViewModel() {

    private var user: User = User(0, "","","")
    private var retrofitInstance: RetrofitInstance = RetrofitInstance(user)

    fun getRetrofit() = retrofitInstance
    fun setRetrofit(retrofitInstance: RetrofitInstance) {
        this.retrofitInstance = retrofitInstance
    }

    fun getUserDetails() = user
    fun setUserDetails(user: User) {
        this.user = user
    }
}
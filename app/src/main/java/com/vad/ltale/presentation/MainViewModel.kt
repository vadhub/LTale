package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance

class MainViewModel : ViewModel() {

    private var userId: Int = 0
    private var userDetails: UserDetails = UserDetails("","")
    private var retrofitInstance: RetrofitInstance = RetrofitInstance(userDetails)

    fun getUserDetails() = userDetails
    fun setUserDetails(userDetails: UserDetails) {
        this.userDetails = userDetails
    }

    fun getRetrofit() = retrofitInstance
    fun setRetrofit(retrofitInstance: RetrofitInstance) {
        this.retrofitInstance = retrofitInstance
    }

    fun getUserId() = userId
    fun setUserId(id: Int) {
        this.userId= id;
    }
}
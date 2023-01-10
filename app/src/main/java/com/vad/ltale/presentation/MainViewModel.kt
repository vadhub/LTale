package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance

class MainViewModel : ViewModel() {

    private var userDetails: UserDetails = UserDetails(0,"","")
    private var retrofitInstance: RetrofitInstance = RetrofitInstance(userDetails)

    fun getRetrofit() = retrofitInstance
    fun setRetrofit(retrofitInstance: RetrofitInstance) {
        this.retrofitInstance = retrofitInstance
    }

    fun getUserDetails() = userDetails
    fun setUserDetails(userDetails: UserDetails) {
        this.userDetails = userDetails
        println(userDetails)
    }
}
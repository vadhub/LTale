package com.vad.ltale.presentation

import androidx.lifecycle.ViewModel
import com.vad.ltale.model.User
import com.vad.ltale.data.remote.RemoteInstance

class MainViewModel : ViewModel() {

    private var user: User = User(0, "","","")
    private var retrofitInstance: RemoteInstance = RemoteInstance(user)

    fun getRetrofit(): RemoteInstance {
        return retrofitInstance
    }
    fun setRetrofit(retrofitInstance: RemoteInstance) {
        this.retrofitInstance = retrofitInstance
    }

    fun getUserDetails() = user
    fun setUserDetails(user: User) {
        this.user = user
    }
}
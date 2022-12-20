package com.vad.ltale.data.repository

import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance

class UserRepository(private val retrofitInstance: RetrofitInstance) {

    suspend fun getUsers() =
        retrofitInstance.apiUser.getUser().body()?.embedded?.users

    suspend fun getUserById(id: Int) =
        retrofitInstance.apiUser.getUser(id).body()?.embedded?.users

    suspend fun creteUser(user: User) =
        retrofitInstance.apiUser.postUser(user)
}
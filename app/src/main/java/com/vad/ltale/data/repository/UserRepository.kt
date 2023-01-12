package com.vad.ltale.data.repository

import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance

class UserRepository(private val retrofitInstance: RetrofitInstance) {

    suspend fun getUsers() =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser().body()?.embedded?.users

    suspend fun getUserById(id: Int) =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser(id).body()?.embedded?.users

    suspend fun getUserByUsername(username: String) =
        retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).login(username).body() ?: User(0, "", "", "")

    suspend fun creteUser(user: User) =
        retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).registration(user)
}
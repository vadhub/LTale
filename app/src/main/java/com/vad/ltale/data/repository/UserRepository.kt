package com.vad.ltale.data.repository

import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RemoteInstance

class UserRepository(private val retrofitInstance: RemoteInstance) {

    suspend fun getUsers() =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser().body()?.embedded?.users

    suspend fun getUserById(id: Int) =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser(id).body()?.embedded?.users

    suspend fun login(username: String): User {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).login(username)
        if (response.code() == 401) {
            throw IllegalArgumentException("user not authorized")
        }

        return response.body() ?: throw IllegalArgumentException(response.errorBody().toString())
    }


    suspend fun creteUser(user: User) : User {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).registration(user)

        if (response.code() == 401) {
            throw IllegalArgumentException("user not authorized")
        }

        return response.body() ?: throw IllegalArgumentException(response.errorBody().toString())
    }
}
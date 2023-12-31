package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.model.User

class UserRepository(private val retrofitInstance: RemoteInstance) {

    suspend fun getUserById(id: Long): User? =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser(id).body()

    suspend fun login(username: String): User {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).login(username)
        if (response.code() == 401) {
            throw IllegalArgumentException("user not authorized")
        }

        return response.body() ?: throw IllegalArgumentException(response.errorBody().toString())
    }


    suspend fun createUser(user: User) : User {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).registration(user)

        if (response.code() == 401) {
            throw IllegalArgumentException("user not authorized")
        }

        return response.body() ?: throw IllegalArgumentException(response.errorBody().toString())
    }
}
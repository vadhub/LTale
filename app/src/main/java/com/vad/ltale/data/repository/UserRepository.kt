package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.remote.exception.UserAlreadyExistException
import com.vad.ltale.data.remote.exception.UserNotFoundException
import com.vad.ltale.model.User

class UserRepository(private val retrofitInstance: RemoteInstance) {

    suspend fun getUserById(id: Long): User? =
        retrofitInstance.apiUser(retrofitInstance.retrofit()).getUser(id).body()

    suspend fun login(username: String): Resource<User> {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).login(username)
        if (response.code() == 401) {
            return Resource.Failure(UnauthorizedException("user unauthorized"))
        } else if (response.code() == 409) {
            return Resource.Failure(UserNotFoundException("user not found"))
        }

        return Resource.Success(response.body()!!)
    }


    suspend fun createUser(user: User) : Resource<User> {
        val response = retrofitInstance.apiUser(retrofitInstance.retrofitNoAuth()).registration(user)

        if (response.code() == 401) {
            return Resource.Failure(UnauthorizedException("user unauthorized"))
        } else if (response.code() == 409) {
            return Resource.Failure(UserAlreadyExistException("user already exist"))
        }

        return Resource.Success(response.body()!!)
    }
}
package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.remote.exception.UserAlreadyExistException
import com.vad.ltale.data.remote.exception.UserNotFoundException
import com.vad.ltale.model.pojo.User

class UserRepository(private val retrofitInstance: RemoteInstance) {

    suspend fun getUserById(id: Long): User? =
        retrofitInstance.apiUser().getUser(id).body()

    suspend fun login(username: String, password: String): Resource<User> {
        val response = retrofitInstance.userLogin(username, password).login(username)
        if (response.code() == 401) {
            return Resource.Failure(UnauthorizedException("user unauthorized"))
        } else if (response.code() == 409) {
            return Resource.Failure(UserNotFoundException("user not found"))
        }

        return Resource.Success(response.body()!!)
    }

    suspend fun createUser(user: User) : Resource<User> {
        val response = retrofitInstance.userRegistration().registration(user)

        if (response.code() == 401) {
            return Resource.Failure(UnauthorizedException("user unauthorized"))
        } else if (response.code() == 409) {
            return Resource.Failure(UserAlreadyExistException("user already exist"))
        }

        return Resource.Success(response.body()!!)
    }

    suspend fun changeUsername(newNik: String, id: Long) : Resource<User> {
        val response = retrofitInstance.apiUser().changeUsername(newNik, id)

        if (response.code() == 401) {
            return Resource.Failure(UnauthorizedException("user unauthorized"))
        } else if (response.code() == 409) {
            return Resource.Failure(UserAlreadyExistException("user already exist"))
        }

        return Resource.Success(response.body()!!)
    }
}
package com.vad.ltale.data.remote

import com.vad.ltale.data.Main
import com.vad.ltale.data.User
import retrofit2.Response
import retrofit2.http.*

interface UserService : AuthService {
    @GET("api-v1/users")
    suspend fun getUser(): Response<Main>

    @GET("api-v1/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @GET("api-v1/users/search/findByUsername")
    suspend fun getUserByUsername(@Query("username") username: String): User
}
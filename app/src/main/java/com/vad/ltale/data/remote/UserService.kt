package com.vad.ltale.data.remote

import com.vad.ltale.model.pojo.User
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("api-v1/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @PUT("/change")
    suspend fun changeUsername(@Query("newNik") newNik: String, @Query("id") id: Long): Response<User>
}
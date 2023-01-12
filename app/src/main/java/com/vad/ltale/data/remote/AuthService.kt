package com.vad.ltale.data.remote

import com.vad.ltale.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    @POST("/registration")
    suspend fun registration(@Body user: User) : Response<User>

    @GET("/login")
    suspend fun login(@Query("username") user: String) : Response<User>
}
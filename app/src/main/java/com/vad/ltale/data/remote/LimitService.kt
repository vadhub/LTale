package com.vad.ltale.data.remote


import com.vad.ltale.data.Limit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface LimitService {

    @PUT("/api-v1/limits/{id}")
    suspend fun updateTimeById(@Path("id") id: Long): Response<Limit>

    @GET("/api-v1/limitByUserId/{userId}")
    suspend fun getTime(@Path("userId") userId: Long): Response<Limit>

}
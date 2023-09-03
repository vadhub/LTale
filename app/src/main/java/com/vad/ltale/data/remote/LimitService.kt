package com.vad.ltale.data.remote

import com.vad.ltale.model.Limit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface LimitService {

    @PUT("/api-v1/limits/{id}")
    suspend fun update(@Path("id") id: Long, @Body limit: Limit): Response<Limit>

    @GET("/api-v1/limit/{userId}")
    suspend fun getLimit(@Path("userId") userId: Long): Response<Limit>

}
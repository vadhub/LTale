package com.vad.ltale.data.remote

import com.vad.ltale.model.Follow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface FollowService {
    @POST("/api-v1/like")
    suspend fun subscribe(@Body follow: Follow) : Response<Follow>

    @HTTP(method = "DELETE", path = "/api-v1/unsubscribe", hasBody = true)
    suspend fun unsubscribe(@Body follow: Follow) : Response<String>
}
package com.vad.ltale.data.remote

import com.vad.ltale.model.pojo.Follow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface FollowService {

    @GET("/api-v1/subscribers")
    suspend fun getSubscribers(@Query("userId") userId: Long): Response<Long>

    @POST("/api-v1/subscribe")
    suspend fun subscribe(@Body follow: Follow) : Response<Follow>

    @GET("/api-v1/is_subscriber")
    suspend fun isSubscriber(@Query("follower") follower: Long, @Query("followed") followed: Long) : Response<String>

    @HTTP(method = "DELETE", path = "/api-v1/unsubscribe", hasBody = true)
    suspend fun unsubscribe(@Body follow: Follow) : Response<String>
}
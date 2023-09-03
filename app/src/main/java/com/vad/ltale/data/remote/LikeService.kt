package com.vad.ltale.data.remote

import com.vad.ltale.data.Like
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface LikeService {

    @POST("/api-v1/like")
    suspend fun likePost(@Body like: Like) : Response<Like>

    @HTTP(method = "DELETE", path = "/api-v1/delete-like", hasBody = true)
    suspend fun deleteLike(@Body like: Like) : Response<String>

}
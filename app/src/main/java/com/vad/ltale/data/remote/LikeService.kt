package com.vad.ltale.data.remote

import com.vad.ltale.data.Like
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface LikeService {

    @POST("/like")
    suspend fun likePost(@Body like: Like) : Response<Like>

    @DELETE("/delete-like")
    suspend fun deleteLike(@Body like: Like) : Response<String>

}
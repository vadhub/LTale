package com.vad.ltale.data.remote

import com.vad.ltale.data.Main
import com.vad.ltale.data.PostRequest
import com.vad.ltale.data.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface JsonPlaceHolderPost {
    @GET("api-v1/posts")
    suspend fun getPost(): Response<Main>

    @GET("api-v1/posts/{id}")
    suspend fun getPostById(@Part id: Int): Response<Main>

    @GET("api-v1/posts/search/findAllByUserId")
    suspend fun getPostByUserId(@Query("userId") userId: Int): Response<Main>

    @POST("api-v1/post/save")
    suspend fun postPost(@Body postRequest: PostRequest) : Response<PostResponse>
}
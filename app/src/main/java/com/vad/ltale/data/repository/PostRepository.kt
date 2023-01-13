package com.vad.ltale.data.repository

import com.vad.ltale.data.PostRequest
import com.vad.ltale.data.remote.RetrofitInstance

class PostRepository(private val retrofitInstance: RetrofitInstance) {
    suspend fun getPosts() =
        retrofitInstance.apiPost().getPost().body()?.embedded?.messages ?: emptyList()

    suspend fun getPostById(id: Int) =
        retrofitInstance.apiPost().getPostById(id).body()?.embedded?.messages ?: emptyList()

    suspend fun getPostByUserId(userId: Int) =
        retrofitInstance.apiPost().getPostByUserId(userId).body()?.embedded?.messages ?: emptyList()

    suspend fun postPost(postRequest: PostRequest) =
        retrofitInstance.apiPost().postPost(postRequest)
}
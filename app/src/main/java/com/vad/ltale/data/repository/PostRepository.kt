package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RetrofitInstance

class PostRepository(private val retrofitInstance: RetrofitInstance) {
    suspend fun getMessages() =
        retrofitInstance.apiMessage().getPost().body()?.embedded?.messages ?: emptyList()

    suspend fun getMessageById(id: Int) =
        retrofitInstance.apiMessage().getPostById(id).body()?.embedded?.messages ?: emptyList()

    suspend fun getMessageByUserId(userId: Int) =
        retrofitInstance.apiMessage().getPostByUserId(userId).body()?.embedded?.messages ?: emptyList()
}
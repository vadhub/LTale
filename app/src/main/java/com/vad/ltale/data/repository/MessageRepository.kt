package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RetrofitInstance

class MessageRepository(private val retrofitInstance: RetrofitInstance): RepositoryBasic(retrofitInstance) {
    suspend fun getMessages() =
        retrofitInstance.apiMessage().getMessage().body()?.embedded?.messages ?: emptyList()

    suspend fun getMessageById(id: Int) =
        retrofitInstance.apiMessage().getMessageById(id).body()?.embedded?.messages ?: emptyList()

    suspend fun getMessageByUserId(userId: Int) =
        retrofitInstance.apiMessage().getMessageByUserId(userId).body()?.embedded?.messages ?: emptyList()
}
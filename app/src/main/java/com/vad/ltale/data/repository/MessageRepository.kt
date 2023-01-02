package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RetrofitInstance

class MessageRepository(private val retrofitInstance: RetrofitInstance) {
    suspend fun getMessages() =
        retrofitInstance.apiMessage.getMessage().body()?.embedded?.messages

    suspend fun getMessageById(id: Int) =
        retrofitInstance.apiMessage.getMessageById(id).body()?.embedded?.messages

    suspend fun getMessageByUserId(userId: Int) =
        retrofitInstance.apiMessage.getMessageByUserId(userId).body()?.embedded?.messages
}
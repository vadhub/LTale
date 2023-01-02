package com.vad.ltale.data.remote

import com.vad.ltale.data.Main
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Part
import retrofit2.http.Path

interface JsonPlaceHolderMessage {
    @GET("api-v1/messages")
    suspend fun getMessage(): Response<Main>

    @GET("api-v1/messages/{id}")
    suspend fun getMessageById(@Part id: Int): Response<Main>

    @GET("api-v1/messages/search/findAllByUserId")
    suspend fun getMessageByUserId(@Path("userId") userId: Int): Response<Main>
}
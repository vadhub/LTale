package com.vad.ltale.data.remote

import com.vad.ltale.data.Main
import com.vad.ltale.data.Message
import com.vad.ltale.data.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface JsonPlaceHolderMessage {
    @GET("api-v1/messages")
    suspend fun getMessage(): Response<Main>

    @GET("api-v1/messages/{id}")
    suspend fun getMessage(@Part id: Int): Response<Main>

    @Multipart
    @POST("api-v1/messages")
    suspend fun postMessage(@Part serData: MultipartBody.Part, @Body message: Message)
}
package com.vad.ltale.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    private val interceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("http://10.0.2.2:8080/")
            .build()
    }

    val apiUser: JsonPlaceHolderUser by lazy {
        retrofit.create(JsonPlaceHolderUser::class.java)
    }

    val apiMessage: JsonPlaceHolderMessage by lazy {
        retrofit.create(JsonPlaceHolderMessage::class.java)
    }

    val apiUpload: FileService by lazy {
        retrofit.create(FileService::class.java)
    }
}
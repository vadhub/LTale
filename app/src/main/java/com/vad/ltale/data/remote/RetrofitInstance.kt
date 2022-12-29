package com.vad.ltale.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
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

    val apiDownload: FileService by lazy {
        retrofit.create(FileService::class.java)
    }
}
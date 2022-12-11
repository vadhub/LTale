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

    val api: JsonPlaceHolder by lazy {
        retrofit.create(JsonPlaceHolder::class.java)
    }
}
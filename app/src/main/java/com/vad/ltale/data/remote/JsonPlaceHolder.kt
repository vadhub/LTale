package com.vad.ltale.data.remote

import com.vad.ltale.data.Embedded
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonPlaceHolder {
    @GET("v1/current.json")
    suspend fun getWeather(@Query("key") key: String, @Query("q") q: String, @Query("aqi") aqi: String): Response<Embedded>
}
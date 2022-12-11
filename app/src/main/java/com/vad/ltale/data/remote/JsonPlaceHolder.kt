package com.vad.ltale.data.remote

import com.vad.ltale.data.Embedded
import com.vad.ltale.data.Main
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Part

interface JsonPlaceHolder {
    @GET("api-v1/employees")
    suspend fun getEmployees(): Response<Main>

    @GET("api-v1/employees/{id}")
    suspend fun getEmployees(@Part id: Int): Response<Main>
}
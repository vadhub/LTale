package com.vad.ltale.data.remote

import com.vad.ltale.data.UserDetails
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance(private val userDetails: UserDetails) {

    private val interceptorBody: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun basicAuthInterceptor(username: String, password: String): Interceptor {
        println("$username $password")
        return BasicAuthInterceptor(username, password)
    }

    private fun client(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(interceptorBody)
            .build()

    private fun clientNoAuth(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptorBody)
            .build()

    fun retrofit(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client(basicAuthInterceptor(userDetails.username, userDetails.password)))
            .baseUrl("http://10.0.2.2:8080/")
            .build()

    fun retrofitNoAuth(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientNoAuth())
            .baseUrl("http://10.0.2.2:8080/")
            .build()

    fun apiUser(retrofit: Retrofit): JsonPlaceHolderUser =
        retrofit.create(JsonPlaceHolderUser::class.java)

    fun apiMessage(): JsonPlaceHolderMessage =
        retrofit().create(JsonPlaceHolderMessage::class.java)

    fun apiUpload(): FileService =
        retrofit().create(FileService::class.java)
}
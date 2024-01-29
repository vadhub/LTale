package com.vad.ltale.data.remote

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.google.gson.GsonBuilder
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.vad.ltale.R
import com.vad.ltale.model.pojo.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RemoteInstance {

    var user: User = User(-1, "", "", "")
        private set

    fun setUser(user: User) {
        this.user = user
    }

    private const val baseUrl: String = "http://82.97.248.120:8090/"

    //"http://10.0.2.2:8080/"

    private val interceptorBody: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun basicAuthInterceptor(username: String, password: String): Interceptor {
        return BasicAuthInterceptor(username, password)
    }

    private fun client(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(interceptorBody)
            .build()

    private val gson = GsonBuilder().setLenient().create()

    private fun retrofitBase(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

    private fun retrofitWithAuth(): Retrofit.Builder =
        retrofitBase().client(client(basicAuthInterceptor(user.username, user.password)))

    private fun retrofitForLogin(username: String, password: String): Retrofit.Builder =
        retrofitBase().client(client(basicAuthInterceptor(username, password)))

    fun setPicasso(context: Context) {
        try {
            val picasso = Picasso.Builder(context)
                .memoryCache(LruCache(context))
                .downloader(
                    OkHttp3Downloader(
                        client(basicAuthInterceptor(user.username, user.password))
                    )
                ).build()
            Picasso.setSingletonInstance(picasso)
        } catch (_: IllegalStateException) {}

    }

    fun apiIcon(imageView: ImageView, userId: Long) {
        imageView.context.let {
            Picasso.get()
                .load("${baseUrl}api-v1/files/icon/search?userId=$userId")
                .error(R.drawable.account_circle_fill0_wght200_grad0_opsz24)
                .into(imageView)
        }
    }

    fun apiImage(imageView: ImageView, imageId: Long?) {
        imageView.context.let {
            Picasso.get()
                .load("${baseUrl}api-v1/files/image/search?id=$imageId")
                .into(imageView)
        }
    }

    fun apiUser(): UserService {
        return retrofitWithAuth().build().create(UserService::class.java)
    }

    fun userRegistration(): AuthService =
        retrofitBase().build().create(AuthService::class.java)

    fun userLogin(username: String, password: String): AuthService =
        retrofitForLogin(username, password).build().create(AuthService::class.java)

    fun apiPost(): PostService =
        retrofitWithAuth().build().create(PostService::class.java)

    fun apiFileHandle(): FileService =
        retrofitWithAuth().build().create(FileService::class.java)

    fun apiLike(): LikeService =
        retrofitWithAuth().build().create(LikeService::class.java)

    fun apiLimit(): LimitService =
        retrofitWithAuth().build().create(LimitService::class.java)

    fun apiFollow(): FollowService =
        retrofitWithAuth().build().create(FollowService::class.java)

}
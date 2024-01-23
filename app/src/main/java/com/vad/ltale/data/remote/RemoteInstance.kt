package com.vad.ltale.data.remote

import android.content.Context
import android.widget.ImageView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Callback
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
import java.util.concurrent.TimeUnit

object RemoteInstance {

    var user: User = User(-1, "", "", "")
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

    private fun clientNoAuth(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptorBody)
            .build()

    private val gson = GsonBuilder().setLenient().create()

    fun retrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client(basicAuthInterceptor(user.username, user.password)))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    fun retrofitNoAuth(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientNoAuth())
            .baseUrl(baseUrl)
            .build()

    private fun picasso(context: Context): Picasso {
        return Picasso.Builder(context)
            .downloader(
                OkHttp3Downloader(
                    client(basicAuthInterceptor(user.username, user.password))
                )
            ).build()
    }

    fun apiIcon(imageView: ImageView, callback: Callback, userId: Long) {
        imageView.context.let {
            picasso(it)
                .load("${baseUrl}api-v1/files/icon/search?userId=$userId")
                .error(R.drawable.account_circle_fill0_wght200_grad0_opsz24)
                .into(imageView, callback)
        }
    }

    fun apiImage(imageView: ImageView, imageId: Long?) {
        imageView.context.let {
            picasso(it)
                .load("${baseUrl}api-v1/files/image/search?id=$imageId")
                .into(imageView)
        }
    }

    fun apiUser(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    fun apiPost(): PostService =
        retrofit().create(PostService::class.java)

    fun apiFileHandle(): FileService =
        retrofit().create(FileService::class.java)

    fun apiLike(): LikeService =
        retrofit().create(LikeService::class.java)

    fun apiLimit(): LimitService =
        retrofit().create(LimitService::class.java)

    fun apiFollow(): FollowService =
        retrofit().create(FollowService::class.java)

}
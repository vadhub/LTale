package com.vad.ltale.data.remote

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BasicAuthInterceptor(private val username: String, private val password: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential: String = Credentials.basic(username, password)
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder().header("Authorization", credential).build()
        return chain.proceed(authenticatedRequest)

    }
}
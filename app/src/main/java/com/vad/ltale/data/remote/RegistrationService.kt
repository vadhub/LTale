package com.vad.ltale.data.remote

import com.vad.ltale.data.User
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {

    @POST("/registration")
    suspend fun registration(@Body user: User)
}
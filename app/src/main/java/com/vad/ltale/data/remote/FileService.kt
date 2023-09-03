package com.vad.ltale.data.remote;

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Multipart
    @POST("/api-v1/upload/icon")
    suspend fun uploadIcon(
        @Part file: MultipartBody.Part,
        @Part("dateCreated") dateCreated: RequestBody,
        @Part("dateChanged") dateChanged: RequestBody,
        @Part("userId") userId: RequestBody
    )

    @GET("/api-v1/files/audio/search")
    @Streaming
    suspend fun downloadAudio(
        @Query("id") id: Long,
    ) : Response<ResponseBody>

}

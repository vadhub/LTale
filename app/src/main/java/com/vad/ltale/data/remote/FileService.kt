package com.vad.ltale.data.remote;

import com.vad.ltale.data.AudioRequest
import com.vad.ltale.data.ImageRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Multipart
    @POST("/upload/audio")
    suspend fun uploadAudio(
        @Body audioRequest: AudioRequest
    )

    @Multipart
    @POST("/upload/image")
    suspend fun uploadImage(
        @Body imageRequest: ImageRequest
    )

    @GET("/files/search")
    suspend fun downloadFile(
        @Query("user") userId: String,
        @Query("directory") directory: String,
        @Query("filename") fileName: String
    ) : Response<ResponseBody>

}

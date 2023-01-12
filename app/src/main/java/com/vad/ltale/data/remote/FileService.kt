package com.vad.ltale.data.remote;

import com.vad.ltale.data.FileRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Multipart
    @POST("/upload/audio")
    suspend fun uploadAudio(
        @Body file: FileRequest
    )

    @Multipart
    @POST("/upload/image")
    suspend fun uploadImage(
        @Body file: FileRequest
    )

    @GET("/files/search")
    suspend fun downloadFile(
        @Query("user") userId: String,
        @Query("directory") directory: String,
        @Query("filename") fileName: String
    ) : Response<ResponseBody>

}

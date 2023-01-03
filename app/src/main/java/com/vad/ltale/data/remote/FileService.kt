package com.vad.ltale.data.remote;

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Multipart
    @POST("/upload/audio")
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part,
        @Query("title") title: String,
        @Query("id_user") idUser: Int
    )

    @Multipart
    @POST("/upload/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("id_user") idUser: Int,
        @Query("is_icon") isIcon: Int
    )

    @GET("/files/search")
    suspend fun downloadFile(
        @Query("user") userId: String,
        @Query("directory") directory: String,
        @Query("filename") fileName: String
    ) : Response<ResponseBody>

}

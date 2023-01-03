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
        @Part("title") title: RequestBody,
        @Part("id_user") idUser: RequestBody
    )

    @Multipart
    @POST("/upload/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody
    )

    @GET("/files/{user}/{directory}/{filename}")
    suspend fun downloadFile(
        @Path("filename") fileName: String,
        @Path("directory") directory: String,
        @Path("user") userId: String
    ) : Response<ResponseBody>

}

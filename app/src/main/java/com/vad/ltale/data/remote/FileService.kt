package com.vad.ltale.data.remote;

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Streaming
    @Multipart
    @POST("/upload/audio")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("id_user") idUser: RequestBody
    )

    @Streaming
    @GET("/files/{user}/{directory}/{filename}")
    suspend fun downloadFile(
        @Path("filename") fileName: String,
        @Path("directory") directory: String,
        @Path("user") userId: String
    ) : Response<ResponseBody>

}

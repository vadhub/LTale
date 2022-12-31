package com.vad.ltale.data.remote;

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part
import retrofit2.http.Path

interface FileService {
    @Multipart
    @POST("/upload/audio")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("id_user") idUser: RequestBody
    )

    @GET("/files/{user}/{directory}/{filename}")
    suspend fun downloadFile(
        @Path("filename") fileName: String,
        @Path("directory") directory: String,
        @Path("user") userId: String
    ) : Response<ResponseBody>

}

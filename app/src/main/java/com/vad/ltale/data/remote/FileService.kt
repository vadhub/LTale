package com.vad.ltale.data.remote;


import com.vad.ltale.data.FileRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    @Multipart
    @POST("/api-v1/upload/audio")
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part,
        @Part("dateCreated") dateCreated: RequestBody,
        @Part("dateChanged") dateChanged: RequestBody
    )

    @Multipart
    @POST("/api-v1/upload/image")
    suspend fun uploadImage(
        @Body file: FileRequest
    )

    @Multipart
    @POST("/api-v1/upload/icon")
    suspend fun uploadIcon(
        @Part file: MultipartBody.Part,
        @Part("dateCreated") dateCreated: RequestBody,
        @Part("dateChanged") dateChanged: RequestBody,
        @Part("userId") userId: RequestBody
    )

    @GET("/files/search")
    suspend fun downloadFile(
        @Query("user") userId: String,
        @Query("directory") directory: String,
        @Query("filename") fileName: String
    ) : Response<ResponseBody>

}

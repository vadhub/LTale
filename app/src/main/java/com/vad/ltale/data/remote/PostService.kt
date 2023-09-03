package com.vad.ltale.data.remote

import com.vad.ltale.data.Main
import com.vad.ltale.data.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("api-v1/posts")
    suspend fun getPost(): Response<Main>

    @GET("api-v1/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Main>

    @GET("api-v1/posts/search/findAllByUserId")
    suspend fun getPostsByUserId(@Query("userId") userId: Long): Response<Main>

    @Multipart
    @POST("api-v1/post/save")
    suspend fun postPost(
        @Part audio: List<MultipartBody.Part>,
        @Part("duration") duration: List<@JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?,
        @Part("userId") userId: RequestBody,
        @Part("dateCreated") dateCreated: RequestBody,
        @Part("dateChanged") dateChanged: RequestBody,
        @Part("hashtags") hashtags: List<@JvmSuppressWildcards RequestBody>?
    ) : Response<PostResponse?>
}
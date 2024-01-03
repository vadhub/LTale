package com.vad.ltale.data.remote

import com.vad.ltale.model.Main
import com.vad.ltale.model.PostResponse
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
    suspend fun getPost(@Query("currentUserId") currentUserId: Long, @Query("page") page: Int): Response<List<PostResponse>>

    @GET("api-v1/posts/user")
    suspend fun getPostsByUserId(@Query("userId") userId: Long, @Query("currentUserId") currentUserId: Long, @Query("page") page: Int): Response<List<PostResponse>>

    @GET("api-v1/posts/search/findAllPostByText")
    suspend fun getPostsByText(@Query("text") text: String): Response<Main>

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
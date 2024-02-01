package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.model.pojo.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class PostRepository(private val retrofitInstance: RemoteInstance) {
    suspend fun getPosts(currentUserId: Long, page: Int) =
        retrofitInstance.apiPost().getPost(currentUserId, page).body() ?: emptyList()

    suspend fun getPostByUserId(userId: Long, currentUserId: Long, page: Int) =
        retrofitInstance.apiPost().getPostsByUserId(userId, currentUserId, page).body()
            ?: emptyList()

    suspend fun getPostsByText(text: String) =
        retrofitInstance.apiPost().getPostsByText(text).body()?.embedded?.messages ?: emptyList()

    suspend fun sendPost(
        audio: List<MultipartBody.Part>,
        duration: List<RequestBody>,
        image: MultipartBody.Part?,
        userId: RequestBody,
        dateCreated: RequestBody,
        dateChanged: RequestBody,
        hashtags: List<RequestBody>?
    ): Resource<PostResponse> {

        val response = retrofitInstance.apiPost()
            .postPost(audio, duration, image, userId, dateCreated, dateChanged, hashtags)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return Resource.Success(body)
        }

        return Resource.Failure(Exception("fail"))
    }

    suspend fun getCountOfPost(userId: Long) =
        retrofitInstance.apiPost().getCountOfPosts(userId).body()

    suspend fun deletePost(id: Long) =
        retrofitInstance.apiPost().deletePost(id)
}
package com.vad.ltale.data.repository

import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.remote.RemoteInstance
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepository(private val retrofitInstance: RemoteInstance) {
    suspend fun getPosts() =
        retrofitInstance.apiPost().getPost().body()?.embedded?.messages ?: emptyList()

    suspend fun getPostById(id: Long) =
        retrofitInstance.apiPost().getPostById(id).body()?.embedded?.messages ?: emptyList()

    suspend fun getPostByUserId(userId: Long): List<PostResponse> {
        return retrofitInstance.apiPost().getPostsByUserId(userId).body()?.embedded?.messages ?: emptyList()
    }

    suspend fun sendPost(audio: List<MultipartBody.Part>,
                         duration: List<RequestBody>,
                         image: MultipartBody.Part?,
                         userId: RequestBody,
                         dateCreated: RequestBody,
                         dateChanged: RequestBody,
                         hashtags: List<RequestBody>?
    ) =
        retrofitInstance.apiPost().postPost(audio, duration, image, userId, dateCreated, dateChanged, hashtags)
}
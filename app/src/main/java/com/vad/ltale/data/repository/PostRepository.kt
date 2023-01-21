package com.vad.ltale.data.repository

import com.vad.ltale.data.FileResponse
import com.vad.ltale.data.Post
import com.vad.ltale.data.remote.RemoteInstance
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepository(private val retrofitInstance: RemoteInstance) {
    suspend fun getPosts() =
        retrofitInstance.apiPost().getPost().body()?.embedded?.messages ?: emptyList()

    suspend fun getPostById(id: Int) =
        retrofitInstance.apiPost().getPostById(id).body()?.embedded?.messages ?: emptyList()

    suspend fun getPostByUserId(userId: Int): List<Post> {
        val postResponse = retrofitInstance.apiPost().getPostsByUserId(userId).body()?.embedded?.messages ?: emptyList()
        return postResponse.map { p -> Post(p.dateChanged, p.imageId, listOf(FileResponse(""))) }
    }


    suspend fun postPost(audio: List<MultipartBody.Part>,
                         image: MultipartBody.Part?,
                         userId: RequestBody,
                         dateCreated: RequestBody,
                         dateChanged: RequestBody) =
        retrofitInstance.apiPost().postPost(audio, image, userId, dateCreated, dateChanged)
}
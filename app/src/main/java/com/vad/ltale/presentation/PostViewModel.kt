package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.PostRequest
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.sql.Date

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()

    fun getPosts() = viewModelScope.launch {
        posts.postValue(postRepository.getPosts())
    }

    fun getPostById(id: Int) = viewModelScope.launch {
        posts.postValue(postRepository.getPostById(id))
    }

    fun getPostByUserId(userId: Int) = viewModelScope.launch {
        posts.postValue(postRepository.getPostByUserId(userId))
    }

    fun savePost(audio: File, image: File?, userId: Int) = viewModelScope.launch {

        val requestAudio: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), audio)

        val audioBody: MultipartBody.Part =
            MultipartBody.Part.createFormData("audio", audio.name, requestAudio)

        var imageBody: MultipartBody.Part? = null
        if (image?.exists() == true) {
            val requestImage: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), image)

            imageBody =
                MultipartBody.Part.createFormData("image", audio.name, requestImage)
        }

        val requestUserId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$userId")

        val requestDateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")


        postRepository.postPost(audioBody, imageBody, requestUserId, requestDateCreated, requestDateChanged)
    }
}
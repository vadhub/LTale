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
            MultipartBody.Part.createFormData("file", audio.name, requestAudio)

        val requestImage: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), audio)

        val imageBody: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", audio.name, requestImage)

        val postRequest = PostRequest(
            audioBody,
            imageBody,
            userId,
            Date(System.currentTimeMillis()),
            Date(System.currentTimeMillis())
        )

        postRepository.postPost(postRequest)
    }
}
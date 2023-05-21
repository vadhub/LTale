package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.AudioRequest
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
    val post: MutableLiveData<Int> = MutableLiveData()

    fun getPostsByUserId(userId: Long) = viewModelScope.launch {
        posts.postValue(postRepository.getPostByUserId(userId))
    }

    fun getPostById(postId: Long, position: Int) = viewModelScope.launch {
        postRepository.getPostById(postId)
        post.postValue(position)
    }

    fun savePost(audio: List<AudioRequest>, image: File?, userId: Long) = viewModelScope.launch {

        val listAudio = audio.map{ a ->
                MultipartBody.Part.createFormData("audio", a.file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), a.file))
        }

        val listDuration = audio.map {
            a -> RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${a.duration}")
        }

        var imageBody: MultipartBody.Part? = null
        if (image?.exists() == true) {
            val requestImage: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), image)

            imageBody =
                MultipartBody.Part.createFormData("image", image.name, requestImage)
        }

        val requestUserId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$userId")

        val requestDateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        postRepository.sendPost(listAudio, listDuration, imageBody, requestUserId, requestDateCreated, requestDateChanged)
    }
}
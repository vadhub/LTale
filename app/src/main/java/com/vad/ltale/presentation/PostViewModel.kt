package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.AudioRequest
import com.vad.ltale.data.Post
import com.vad.ltale.data.PostResponse
import com.vad.ltale.data.repository.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()

    fun getPostsByUserId(userId: Int) = viewModelScope.launch {
        posts.postValue(postRepository.getPostByUserId(userId))
    }

    fun savePost(audio: MutableList<AudioRequest>, image: File?, userId: Int) = viewModelScope.launch {

        val listAudio = audio.map{ a ->
            MultipartBody.Part.createFormData("audio", a.file.name,
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), a.file)
            )
        }.toList()

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
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")


        postRepository.postPost(listAudio, imageBody, requestUserId, requestDateCreated, requestDateChanged)
    }
}
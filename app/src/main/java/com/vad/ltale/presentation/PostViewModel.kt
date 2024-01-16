package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.model.AudioRequest
import com.vad.ltale.model.PostResponse
import com.vad.ltale.data.repository.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.sql.Date

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    val countOfPosts: MutableLiveData<Int> = MutableLiveData()
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postsByUserId: MutableLiveData<List<PostResponse>> = MutableLiveData()
    private var page = 0
    private var pageOfUserPosts = 0

    fun getPostsByText(text: String) = viewModelScope.launch {
        posts.postValue(postRepository.getPostsByText(text))
    }

    fun getPosts(currentUserId: Long) = viewModelScope.launch {

        val loadedPosts: MutableList<PostResponse>? = posts.value as? MutableList<PostResponse>
        val loaded = postRepository.getPosts(currentUserId, page)

        if (loaded.isNotEmpty()) {
            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                posts.postValue(loadedPosts!!)
            } else {
                posts.postValue(loaded)
            }

            page++
        }
    }

    fun getPostsByUserId(userId: Long, currentUserId: Long) = viewModelScope.launch {

        val loadedPosts: MutableList<PostResponse>? = posts.value as? MutableList<PostResponse>
        val loaded = postRepository.getPostByUserId(userId, currentUserId, pageOfUserPosts)

        if (loaded.isNotEmpty()) {
            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                postsByUserId.postValue(loadedPosts!!)
            } else {
                postsByUserId.postValue(loaded)
            }

            pageOfUserPosts++
        }

    }

    fun savePost(audio: List<AudioRequest>, image: File?, userId: Long, hashtags: List<String>?) = viewModelScope.launch {

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

        var hashtagRequest: List<RequestBody>? = null
        if (hashtags != null) {
            hashtagRequest = hashtags.map { h ->
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), h)
            }
        }
        postRepository.sendPost(listAudio, listDuration, imageBody, requestUserId, requestDateCreated, requestDateChanged, hashtagRequest)
    }

    fun getCountOfPostsByUserId(userId: Long) = viewModelScope.launch {
        countOfPosts.postValue(postRepository.getCountOfPost(userId))
    }
}

class PostViewModelFactory(private val postRepository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(postRepository) as T
    }
}
package com.vad.ltale.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.pojo.AudioRequest
import com.vad.ltale.model.pojo.PostResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.sql.Date

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    val countOfPosts: MutableLiveData<Int> = MutableLiveData()
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postsByUserId: MutableLiveData<List<PostResponse>> = MutableLiveData()
    private var page = 0
    private var pageOfUserPosts = 0
    private var userId = -1L

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

        if (this@PostViewModel.userId != userId) {
            clearPostsOfUSer()
            this@PostViewModel.userId = userId
        }

        val loadedPosts: MutableList<PostResponse>? = postsByUserId.value as? MutableList<PostResponse>
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

    fun clearPostsOfUSer() {
        postsByUserId.value = emptyList()
        pageOfUserPosts = 0
    }

    fun savePost(audio: List<AudioRequest>, image: File?, userId: Long, hashtags: List<String>?) = viewModelScope.launch {

        val listAudio = audio.map{ a ->
                MultipartBody.Part.createFormData("audio", a.file.name, a.file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        }

        val listDuration = audio.map {
            a -> "${a.duration}".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        var imageBody: MultipartBody.Part? = null
        if (image?.exists() == true) {
            val requestImage: RequestBody = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("image", image.name, requestImage)
        }

        val requestUserId: RequestBody =
            "$userId".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val requestDateCreated: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val requestDateChanged: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var hashtagRequest: List<RequestBody>? = null
        if (hashtags != null) {
            hashtagRequest = hashtags.map { h ->
                h.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
        }
        postRepository.sendPost(listAudio, listDuration, imageBody, requestUserId, requestDateCreated, requestDateChanged, hashtagRequest)
    }

    fun getCountOfPostsByUserId(userId: Long) = viewModelScope.launch {
        countOfPosts.postValue(postRepository.getCountOfPost(userId))
    }
}

@Suppress("UNCHECKED_CAST")
class PostViewModelFactory(private val postRepository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(postRepository) as T
    }
}
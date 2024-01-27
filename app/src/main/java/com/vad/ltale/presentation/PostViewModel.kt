package com.vad.ltale.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.repository.PostRepository
import com.vad.ltale.model.pojo.AudioRequest
import com.vad.ltale.model.pojo.PostResponse
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import ir.logicbase.livex.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.sql.Date
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    val countOfPosts: MutableLiveData<Int> = MutableLiveData()
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postsByUserId: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postResponse: SingleLiveEvent<Resource<PostResponse>> = SingleLiveEvent()

    private var page = AtomicInteger(0)
    private var pageOfUserPosts = AtomicInteger(0)
    private var userId = AtomicLong(-1L)

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun getPostsByText(text: String) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        posts.postValue(postRepository.getPostsByText(text))
    }

    fun getPosts(currentUserId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        val loadedPosts: MutableList<PostResponse>? = posts.value as? MutableList<PostResponse>
        val loaded = postRepository.getPosts(currentUserId, page.get())

        if (loaded.isNotEmpty()) {
            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                posts.postValue(loadedPosts!!)
            } else {
                posts.postValue(loaded)
            }

            page.incrementAndGet()
        }
    }

    fun getPostsByUserId(userId: Long, currentUserId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        if (this@PostViewModel.userId.get() != userId) {
            clearPostsOfUSer()
            Log.d("$$$#ddd","${this@PostViewModel.userId.get()} $userId")
            this@PostViewModel.userId.set(userId)
        }

        val loadedPosts: MutableList<PostResponse>? = postsByUserId.value as? MutableList<PostResponse>
        val loaded = postRepository.getPostByUserId(userId, currentUserId, pageOfUserPosts.get())

        Log.d("$$$#ddd"," 3 ${loadedPosts.toString()}")

        if (loaded.isNotEmpty()) {
            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                postsByUserId.postValue(loadedPosts!!)
            } else {
                postsByUserId.postValue(loaded)
            }

            pageOfUserPosts.incrementAndGet()
        }

    }

    fun clearPostsOfUSer() {
        Log.d("$$$#ddd","clear ${postsByUserId.value}")
        postsByUserId = MutableLiveData()
        Log.d("$$$#ddd","clear 2 ${postsByUserId.value}")
        pageOfUserPosts.set(0)
    }

    fun clearPosts() {
        posts.postValue(emptyList())
        page.set(0)
    }

    fun savePost(
        context: Context,
        audio: List<AudioRequest>,
        image: File?,
        userId: Long,
        hashtags: List<String>?
    ) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        postResponse.postValue(Resource.Loading)

        val listAudio = audio.map { a ->
            MultipartBody.Part.createFormData(
                "audio",
                a.file.name,
                a.file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }

        val listDuration = audio.map { a ->
            "${a.duration}".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        var imageBody: MultipartBody.Part? = null
        if (image?.exists() == true) {

            val compressImage = Compressor.compress(context, image) {
                quality(50)
            }

            val requestImage: RequestBody =
                compressImage.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("image", compressImage.name, requestImage)
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

        postResponse.postValue(
            postRepository.sendPost(
                listAudio,
                listDuration,
                imageBody,
                requestUserId,
                requestDateCreated,
                requestDateChanged,
                hashtagRequest
            )
        )
    }

    fun getCountOfPostsByUserId(userId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        countOfPosts.postValue(postRepository.getCountOfPost(userId))
    }
}

@Suppress("UNCHECKED_CAST")
class PostViewModelFactory(private val postRepository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(postRepository) as T
    }
}
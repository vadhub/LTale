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
import id.zelory.compressor.constraint.size
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

    private var sortType = 1

    val countOfPosts: MutableLiveData<Int> = MutableLiveData()
    var posts: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postsByUserId: MutableLiveData<List<PostResponse>> = MutableLiveData()
    var postResponse: SingleLiveEvent<Resource<PostResponse>> = SingleLiveEvent()
    val postDelete: SingleLiveEvent<Resource<Int>> = SingleLiveEvent()

    private var page = AtomicInteger(0)
    private var pageOfUserPosts = AtomicInteger(0)
    private var userId = AtomicLong(-1L)
    private var currentUserId_ = 0L

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    fun setSortType(type: Int) {
        if (type != sortType) {
            sortType = type
            clearPosts()
            viewModelScope.launch {
                postRepository.getPosts(currentUserId_, page.get(), sortType)
            }
        }
    }

    fun getPostsByText(text: String) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        posts.postValue(postRepository.getPostsByText(text))
    }

    fun getPosts(currentUserId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        currentUserId_ = currentUserId
        val loadedPosts: MutableList<PostResponse>? = posts.value as? MutableList<PostResponse>
        val loaded = postRepository.getPosts(currentUserId, page.get(), sortType)

        if (loaded.isNotEmpty()) {
            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                posts.postValue(loadedPosts!!)
                Log.d("#update", "update1")
            } else {
                posts.postValue(loaded)
                Log.d("#update", "update2")
            }

            page.incrementAndGet()
        }
    }

    fun getPostsByUserIdPaging(userId: Long, currentUserId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

        if (this@PostViewModel.userId.get() != userId) {
            clearPostsOfUSer()
            this@PostViewModel.userId.set(userId)
        }

        val loadedPosts: MutableList<PostResponse>? = postsByUserId.value as? MutableList<PostResponse>
        val loaded = postRepository.getPostByUserId(userId, currentUserId, pageOfUserPosts.get())

        if (loaded.isNotEmpty()) {

            if (!loadedPosts.isNullOrEmpty()) {
                loadedPosts.addAll(loaded)
                postsByUserId.postValue(loadedPosts!!)
            } else {
                postsByUserId.postValue(loaded)
            }

            pageOfUserPosts.incrementAndGet()
        } else if (pageOfUserPosts.get() == 0) {
            postsByUserId.postValue(emptyList())
        }

    }

    fun getPostsByUserId(userId: Long, currentUserId: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        postsByUserId.postValue(postRepository.getPostByUserId(userId, currentUserId, 0))
    }

    private fun clearPostsOfUSer() {
        postsByUserId = MutableLiveData()
        pageOfUserPosts.set(0)
    }

    fun clearPosts() {
        posts = MutableLiveData()
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
                size(500_000)
            }

            val requestImage: RequestBody =
                compressImage.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("image", "${compressImage.name}${System.currentTimeMillis()}", requestImage)
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

    fun removePost(idPost: Long) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        postDelete.postValue(postRepository.deletePost(idPost))
    }
}

@Suppress("UNCHECKED_CAST")
class PostViewModelFactory(private val postRepository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(postRepository) as T
    }
}
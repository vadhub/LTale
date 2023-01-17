package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.FileRequest
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File


class FileViewModel(private val retrofitInstance: RetrofitInstance) : ViewModel() {

    val fileResponseBody: MutableLiveData<ResponseBody> = MutableLiveData()
    var file = File("")

    fun uploadAudio(file: File) = viewModelScope.launch {

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        val requestDateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        retrofitInstance.apiUpload().uploadAudio(body, requestDateCreated, requestDateChanged)
    }

    fun uploadImage(file: File, userId: Int, isIcon: Int) = viewModelScope.launch {
        Log.e("file", file.absolutePath)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        //retrofitInstance.apiUpload().uploadImage(body, userId, isIcon)
    }

    fun uploadIcon(icon: File, userId: Int) = viewModelScope.launch {

        val requestIcon: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), icon)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", icon.name, requestIcon)

        val requestDateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${System.currentTimeMillis()}")

        val requestUserId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$userId")

        retrofitInstance.apiUpload().uploadIcon(body, requestDateCreated, requestDateChanged, requestUserId)
    }

    fun downloadFile(fileName: String, directory: String, userId: String) = viewModelScope.launch(Dispatchers.IO) {
        fileResponseBody.postValue(retrofitInstance.apiUpload().downloadFile(userId, directory, fileName).body())
    }
}
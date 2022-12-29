package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.Message
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FileViewModel(private val retrofitInstance: RetrofitInstance) : ViewModel() {

    fun uploadFile(file: File, message: Message) = viewModelScope.launch {
        Log.e("file", file.absolutePath)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val title: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.title)

        val userId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "1")

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        retrofitInstance.apiUpload.uploadFile(body, title)
    }

    fun downloadFile(fileName: String, userId: String) = viewModelScope.launch {
        println(retrofitInstance.apiUpload.downloadFile(fileName, userId).body())
    }
}
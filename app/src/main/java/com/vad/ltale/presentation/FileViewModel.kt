package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.Message
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

    fun uploadFile(file: File, message: Message) = viewModelScope.launch {
        Log.e("file", file.absolutePath)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val title: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.title)

        val userId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.id.toString())

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        retrofitInstance.apiUpload.uploadFile(body, title, userId)
    }

    fun downloadFile(fileName: String, directory: String, userId: String) = viewModelScope.launch(Dispatchers.IO) {
        fileResponseBody.postValue(retrofitInstance.apiUpload.downloadFile(fileName, directory, userId).body())
    }
}
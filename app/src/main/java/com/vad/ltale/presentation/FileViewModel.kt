package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.FileResponse
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

    fun uploadAudio(file: File, audio: FileResponse) = viewModelScope.launch {
        Log.e("file", file.absolutePath)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        //retrofitInstance.apiUpload().uploadAudio(body, audio)
    }

    fun uploadImage(file: File, userId: Int, isIcon: Int) = viewModelScope.launch {
        Log.e("file", file.absolutePath)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        //retrofitInstance.apiUpload().uploadImage(body, userId, isIcon)
    }

    fun downloadFile(fileName: String, directory: String, userId: String) = viewModelScope.launch(Dispatchers.IO) {
        fileResponseBody.postValue(retrofitInstance.apiUpload().downloadFile(userId, directory, fileName).body())
    }
}
package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.FileRequest
import com.vad.ltale.data.FileResponse
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.sql.Date

class FileViewModel(private val retrofitInstance: RetrofitInstance) : ViewModel() {

    val fileResponseBody: MutableLiveData<ResponseBody> = MutableLiveData()
    var file = File("")

    fun uploadAudio(audio: File) = viewModelScope.launch {

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), audio)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", audio.name, requestFile)

        val file = FileRequest(body, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()))
        retrofitInstance.apiUpload().uploadAudio(file)
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
package com.vad.ltale.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class LoadFileViewModel(private val retrofitInstance: RetrofitInstance) : ViewModel() {

    fun loadFile(file: File) = viewModelScope.launch {
        Log.e("file", file.absolutePath)
        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        retrofitInstance.apiUpload.uploadFile(body)
    }
}
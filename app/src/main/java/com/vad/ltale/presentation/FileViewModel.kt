package com.vad.ltale.presentation

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vad.ltale.data.remote.RemoteInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class FileViewModel(private val remoteInstance: RemoteInstance) : ViewModel() {

    val fileResponseBody: MutableLiveData<ResponseBody> = MutableLiveData()
    var file = File("")

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

        remoteInstance.apiUpload().uploadIcon(body, requestDateCreated, requestDateChanged, requestUserId)
    }

    fun getIcon(userId: Int, context: Context?, imageView: ImageView) = viewModelScope.launch {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/search/icon?userId=$userId")
            .error(com.vad.ltale.R.drawable.ic_launcher_foreground)
            .into(imageView)
        }
    }

    fun getImage(imageId: Int, context: Context?, imageView: ImageView) = viewModelScope.launch {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/search?imageId=$imageId")
            .error(com.vad.ltale.R.drawable.ic_launcher_foreground)
            .into(imageView)
        }
    }

    fun getAudioById(id: Long) = viewModelScope.launch {
        val inputStream: InputStream? = remoteInstance.apiUpload().downloadAudio(id).body()?.byteStream()
        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "ok121")

        try {
            val fileOutputStream = FileOutputStream(file)

            val buffer = ByteArray(1024) // or other buffer size

            var read: Int

            while (inputStream!!.read(buffer).also { read = it } != -1) {
                fileOutputStream.write(buffer, 0, read)
            }

            fileOutputStream.flush()
            Log.d("##444", "control")
        } catch (e: java.lang.Exception) {
            e.stackTrace
        }

    }

}
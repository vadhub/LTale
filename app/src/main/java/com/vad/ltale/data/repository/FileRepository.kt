package com.vad.ltale.data.repository

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.vad.ltale.R
import com.vad.ltale.model.Audio
import com.vad.ltale.data.local.AudioDao
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.remote.RemoteInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.sql.Date

class FileRepository(private val saveHandle: SaveInternalHandle, private val audioDao: AudioDao, private val remoteInstance: RemoteInstance) {

    suspend fun getUriByAudio(audio: Audio): String {

        Log.d("--file repost", "${audioDao.getById(audio.id)}")

        if (audioDao.getById(audio.id) == null) {
            val response = remoteInstance.apiFileHandle().downloadAudio(audio.id)
            if (response.isSuccessful) {
                response.body()?.byteStream()?.let {
                    val file = saveHandle.saveFile(audio.uri, it)
                    audioDao.insert(audio.copy(uri = file))
                    return file
                }
            }
        }

        return audioDao.getById(audio.id)?.uri ?: ""
    }


    private suspend fun insert(audio: Audio) {
        audioDao.insert(audio)
    }

    suspend fun uploadIcon(icon: File, userId: Long) {

        val requestIcon: RequestBody =
            icon.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", icon.name, requestIcon)

        val dateCreated: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val dateChanged: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val userIdL: RequestBody =
            "$userId".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        remoteInstance.apiFileHandle().uploadIcon(body, dateCreated, dateChanged, userIdL)
    }

    fun getIcon(userId: Long, context: Context?, imageView: ImageView) {
        context?.let {
            remoteInstance.picasso(it)
                .load("http://10.0.2.2:8080/api-v1/files/icon/search?userId=$userId")
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }
    }

    fun getImage(imageId: Long?, context: Context?, imageView: ImageView) {
        context?.let {
            remoteInstance.picasso(it)
                .load("http://10.0.2.2:8080/api-v1/files/image/search?id=$imageId")
                .into(imageView)
        }
    }
}
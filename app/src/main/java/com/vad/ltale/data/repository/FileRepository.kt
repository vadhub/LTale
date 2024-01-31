package com.vad.ltale.data.repository

import android.widget.ImageView
import com.vad.ltale.data.local.AudioDao
import com.vad.ltale.data.local.SaveInternalHandle
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.Image
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.sql.Date

class FileRepository(private val saveHandle: SaveInternalHandle, private val audioDao: AudioDao, private val remoteInstance: RemoteInstance) {

    suspend fun getUriByAudio(audio: Audio): String {

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

    suspend fun uploadIcon(icon: File, userId: Long) : Resource<Image> {

        val requestIcon: RequestBody =
            icon.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", "${icon.name}${System.currentTimeMillis()}", requestIcon)

        val dateCreated: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val dateChanged: RequestBody =
            "${Date(System.currentTimeMillis())}".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val userIdL: RequestBody =
            "$userId".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val response = remoteInstance.apiFileHandle().uploadIcon(body, dateCreated, dateChanged, userIdL)

        if (response.isSuccessful) {
            return Resource.Success(response.body()!!)
        }

        return Resource.Failure(IllegalAccessException("bad request"))
    }

    fun getIcon(userId: Long, imageView: ImageView, invalidate: Boolean) {
        remoteInstance.apiIcon(imageView, userId, invalidate)
    }

    fun getImage(imageId: Long?, imageView: ImageView) {
        remoteInstance.apiImage(imageView, imageId)
    }
}
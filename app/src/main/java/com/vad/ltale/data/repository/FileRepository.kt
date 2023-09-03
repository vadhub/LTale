package com.vad.ltale.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.local.AudioDao
import com.vad.ltale.data.remote.RemoteInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.sql.Date

class FileRepository(private val audioDao: AudioDao, private val remoteInstance: RemoteInstance) {

    suspend fun getUriByAudio(audio: Audio): String {

        val file = File(Environment.getExternalStorageDirectory().absolutePath+File.separator+"ltale/audio"+File.separator+audio.uri)

        if (audioDao.getById(audio.id) == null) {
            downloadAudio(file, audio)
            insert(audio)
        }

        return file.path
    }

    private suspend fun downloadAudio(file: File, audio: Audio) {
        val inputStream: InputStream? = remoteInstance.apiUpload().downloadAudio(audio.id).body()?.byteStream()

        try {
            val fileOutputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var read: Int

            while (inputStream!!.read(buffer).also { read = it } != -1) {
                fileOutputStream.write(buffer, 0, read)
            }

            fileOutputStream.flush()
        } catch (e: java.lang.Exception) {
            e.stackTrace
        }
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

        remoteInstance.apiUpload().uploadIcon(body, dateCreated, dateChanged, userIdL)
    }

    fun getIcon(userId: Long, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/icon/search?userId=$userId")
            .error(R.drawable.ic_launcher_foreground)
            .into(imageView)
        }
    }

    fun getImage(imageId: Long?, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/image/search?id=$imageId")
            .into(imageView)
        }
    }
}
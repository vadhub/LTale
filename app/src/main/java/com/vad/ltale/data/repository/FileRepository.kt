package com.vad.ltale.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.vad.ltale.data.Audio
import com.vad.ltale.data.local.AudioDao
import com.vad.ltale.data.remote.RemoteInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

        Log.d("##1", "getUriByAudio: " + file.absolutePath)

        return file.path
    }

    private suspend fun downloadAudio(file: File, audio: Audio) {
        val inputStream: InputStream? = remoteInstance.apiUpload().downloadAudio(audio.id).body()?.byteStream()

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

    private suspend fun insert(audio: Audio) {
        Log.d("##fileRepos", "insert")
        audioDao.insert(audio)
    }

    suspend fun uploadIcon(icon: File, userId: Long) {

        val requestIcon: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), icon)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", icon.name, requestIcon)

        val dateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        val dateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        val userIdL: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$userId")

        remoteInstance.apiUpload().uploadIcon(body, dateCreated, dateChanged, userIdL)
    }

    fun getIcon(userId: Long, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/icon/search?userId=$userId")
            .into(imageView)
        }
    }

    fun getImage(imageId: Long?, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/image/search?id=$imageId")
            .into(imageView)
        }
    }
}
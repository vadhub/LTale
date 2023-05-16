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

        Log.d("##1", "getUriByAudio: " + audioDao.getById(audio.id))

        Log.d("##fileRepos", "getUriByAudio: "+ audio.uri +" " +audio.id + " " + audio.date +" " + audio.duration)

        val file = File(Environment.getExternalStorageDirectory().absolutePath+File.separator+"ltale/audio"+File.separator+audio.uri)
        if (audioDao.getById(audio.id) == null) {
            downloadAudio(file, audio)
            insert(audio)
        }

        return file.absolutePath
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

    suspend fun insert(audio: Audio) {
        Log.d("##fileRepos", "insert")
        audioDao.insert(audio)
    }

    suspend fun uploadIcon(icon: File, userId: Int) {

        val requestIcon: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), icon)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", icon.name, requestIcon)

        val requestDateCreated: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        val requestDateChanged: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "${Date(System.currentTimeMillis())}")

        val requestUserId: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$userId")

        remoteInstance.apiUpload().uploadIcon(body, requestDateCreated, requestDateChanged, requestUserId)
    }

    suspend fun getIcon(userId: Int, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/icon/search?userId=$userId")
            .error(com.vad.ltale.R.drawable.ic_launcher_foreground)
            .into(imageView)
        }
    }

    suspend fun getImage(imageId: Int, context: Context?, imageView: ImageView) {
        context?.let { remoteInstance.picasso(it).load("http://10.0.2.2:8080/api-v1/files/image/search?id=$imageId")
            .error(com.vad.ltale.R.drawable.ic_launcher_foreground)
            .into(imageView)
        }
    }
}
package com.vad.ltale.model.audiohandle

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.vad.ltale.model.AudioRequest
import com.vad.ltale.model.timehandle.ChunkTimer
import java.io.File
import java.io.IOException

class Recorder(
    private val chunkTimer: ChunkTimer,
    private val context: Context
) {
    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private val file =  File(Environment.getExternalStorageDirectory().absolutePath, "ltale/audio")

    init {
        initMediaRecorder()
    }

    private fun initMediaRecorder() {
        if (!file.exists()) {
            file.mkdirs()
        }

        output = context.filesDir.absolutePath + File.separator + "l" + System.currentTimeMillis()

        ///data/user/0/com.vad.ltale/files
        Log.d("ddd", "${context.filesDir}")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
        }
    }

    fun startRecording() {
        if (mediaRecorder == null) {
            initMediaRecorder()
        }
        try {
            chunkTimer.startTimer()
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(): AudioRequest {
        val duration = chunkTimer.cancelTimer()

        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        return AudioRequest(File(output), duration)
    }
}
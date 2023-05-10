package com.vad.ltale.domain.audiohandle

import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import com.vad.ltale.data.AudioRequest
import com.vad.ltale.domain.timehandle.ChunkTimer
import java.io.File
import java.io.IOException


class Recorder(
    private val chunkTimer: ChunkTimer
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

        output = file.absolutePath + File.separator + "l" + System.currentTimeMillis() + ".mp3"

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
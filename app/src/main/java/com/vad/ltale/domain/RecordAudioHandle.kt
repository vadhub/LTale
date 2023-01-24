package com.vad.ltale.domain

import android.media.MediaRecorder
import android.os.Environment
import com.vad.ltale.data.AudioRequest
import java.io.File
import java.io.IOException


class RecordAudioHandle(
    private val chunkTimer: ChunkTimer
) {
    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private val file =  File(Environment.getExternalStorageDirectory().absolutePath, "ltale/audio")

    init {
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

        return AudioRequest(File(output), duration)
    }
}
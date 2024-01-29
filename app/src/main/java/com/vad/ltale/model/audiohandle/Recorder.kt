package com.vad.ltale.model.audiohandle

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.vad.ltale.model.pojo.AudioRequest
import com.vad.ltale.model.timehandle.ChunkTimer
import java.io.File
import java.io.IOException

class Recorder(
    private val chunkTimer: ChunkTimer,
    private val context: Context
) {
    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    private fun getAudioPath() =
        "${context.cacheDir.absolutePath}${File.pathSeparator}l${System.currentTimeMillis()}"

    fun startRecording() {

        output = getAudioPath()

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD)
                setAudioEncodingBitRate(16*44100)
                setAudioSamplingRate(44100)
                setOutputFile(output)
                prepare()
                start()
                chunkTimer.startTimer()
                isRecording = true
            }
            Log.d("##Recorder", "start $this")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getVolume() = mediaRecorder?.maxAmplitude ?: 0

    fun stopRecording(): AudioRequest {
        val duration = chunkTimer.cancelTimer()

        try {
            if (isRecording) {
                mediaRecorder?.stop()
            }
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            Log.d("##Recorder", "stop $this")
        } catch (e: Exception) {
            File(output).delete();

            e.printStackTrace()
        }
        return AudioRequest(File(output), duration)
    }
}
package com.vad.ltale.domain

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import java.io.IOException

class RecordAudioHandle(private val chunkTimer: ChunkTimer, private val contextThis: Context) {

    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null

    init {
        output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/l"+System.currentTimeMillis()+".mp3"
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
            Toast.makeText(contextThis, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(v: View?, actionButton: FloatingActionButton) {
        chunkTimer.cancelTimer()
        mediaRecorder?.stop()
        mediaRecorder?.release()

        actionButton.isActivated = false
        v?.findNavController()?.navigate(R.id.accountFragment)
    }
}
package com.vad.ltale.domain

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.data.FileResponse
import com.vad.ltale.presentation.FileViewModel
import java.io.File
import java.io.IOException


class RecordAudioHandle(
    private val chunkTimer: ChunkTimer,
    private val contextThis: Context,
    private val viewModel: FileViewModel
) {

    private var file: File =
        File(Environment.getExternalStorageDirectory().absolutePath, "ltale/audio")
    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null

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
            Toast.makeText(contextThis, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(v: View?, actionButton: FloatingActionButton, audio: FileResponse) {
        chunkTimer.cancelTimer()
        mediaRecorder?.stop()
        mediaRecorder?.release()

        val tempFile = File(output)

        viewModel.uploadAudio(tempFile, audio)

//        if (tempFile.exists()) {
//            tempFile.delete()
//        }

        actionButton.isActivated = false
        v?.findNavController()?.navigate(R.id.accountFragment)
    }
}
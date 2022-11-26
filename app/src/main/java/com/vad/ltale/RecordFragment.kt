package com.vad.ltale

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException

class RecordFragment : Fragment(), OnTouchListener, TimerHandler {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer

    private var output: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var contextThis: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextThis = context;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        } else {
            mediaRecorder = MediaRecorder()
            output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/record1ingTest.mp3"
            Log.d("---Record", output)
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chunkTimer = ChunkTimer(1000 * 60)
        chunkTimer.setTimerHandler(this)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        val actionButton: FloatingActionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> startRecording()
            MotionEvent.ACTION_UP -> stopRecording()
            MotionEvent.ACTION_CANCEL -> stopRecording()
        }
        return true
    }

    override fun showTime(time: Long) {
        timeRecordTextView.text = "$time"
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }

    private fun startRecording() {

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

    private fun stopRecording() {
        chunkTimer.cancelTimer()
        mediaRecorder?.stop()
        mediaRecorder?.release()
    }
}
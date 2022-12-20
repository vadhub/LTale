package com.vad.ltale.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.R
import com.vad.ltale.domain.ChunkTimer
import com.vad.ltale.domain.RecordAudioHandle
import com.vad.ltale.domain.TimerHandler

class RecordFragment : Fragment(), OnTouchListener, TimerHandler {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer
    private lateinit var actionButton: FloatingActionButton

    private lateinit var contextThis: Context
    private lateinit var recorder: RecordAudioHandle

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
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                contextThis,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        } else {
            chunkTimer = ChunkTimer(1000 * 60)
            recorder = RecordAudioHandle(chunkTimer, contextThis)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chunkTimer.setTimerHandler(this)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        actionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> recorder.startRecording()
            MotionEvent.ACTION_UP -> recorder.stopRecording(v, actionButton)
            MotionEvent.ACTION_CANCEL -> recorder.stopRecording(v, actionButton)
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun showTime(time: Long) {
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        timeRecordTextView.text = "$minutes:$seconds"
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }

}
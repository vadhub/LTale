package com.vad.ltale

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecordFragment : Fragment(), OnTouchListener, TimerHandler {

    private lateinit var timeRecordTextView: TextView
    private lateinit var chunkTimer: ChunkTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chunkTimer = ChunkTimer(1000*60)
        chunkTimer.setTimerHandler(this)
        timeRecordTextView = view.findViewById(R.id.timeLastTextView)
        val actionButton: FloatingActionButton = view.findViewById(R.id.recordFloatingButton)
        actionButton.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> chunkTimer.startTimer()
            MotionEvent.ACTION_UP -> chunkTimer.cancelTimer()
            MotionEvent.ACTION_CANCEL -> chunkTimer.cancelTimer()
        }
        return true
    }

    override fun showTime(time: Long) {
        timeRecordTextView.text = "$time"
    }

    override fun finishTime() {
        timeRecordTextView.text = "end"
    }
}
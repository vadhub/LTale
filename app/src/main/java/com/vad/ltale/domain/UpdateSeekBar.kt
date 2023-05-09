package com.vad.ltale.domain

import android.os.Handler
import android.widget.SeekBar

class UpdateSeekBar(private val seekBar: SeekBar, private val currentPosition: Int, private val handler: Handler): Runnable {

    override fun run() {
        seekBar.progress = currentPosition
        handler.postDelayed(this, 100)
    }
}
package com.vad.ltale.domain

import android.media.MediaPlayer
import android.os.Handler
import android.widget.SeekBar

class UpdateSeekBar(private val seekBar: SeekBar, private val mediaPlayer: MediaPlayer, private val handler: Handler): Runnable {

    override fun run() {
        seekBar.progress = mediaPlayer.currentPosition

        handler.postDelayed(this, 100)
    }
}
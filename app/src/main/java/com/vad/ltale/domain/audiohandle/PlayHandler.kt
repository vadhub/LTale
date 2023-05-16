package com.vad.ltale.domain.audiohandle

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.presentation.adapter.AudioAdapter

class PlayHandler(private val player: Player) {

    private lateinit var handler: Handler
    private lateinit var ran: Runnable

    private var oldPosition = -1
    private var audioTemp = Audio(-1, "", 0, "", false)
    private lateinit var oldAdapter: AudioAdapter

    fun handle(position: Int, audio: Audio, audioAdapter: AudioAdapter, seekBar: SeekBar) {
        handler = Handler(Looper.getMainLooper())
        if (oldPosition != -1) {
            oldAdapter.notifyItemChanged(oldPosition, audioTemp)
        }

        if (player.isPlay) {
            player.stop()
            audioAdapter.notifyItemChanged(position, audio)
        } else {
            player.play(audio.uri)

            ran = Runnable {
                seekBar.progress = ((player.mp.currentPosition*100)/player.mp.duration).toInt()
                handler.postDelayed(ran, 100)
            }

            handler.post(ran)
        }

        oldPosition = position
        oldAdapter = audioAdapter
        audioTemp = audio.also { it.isPlay = false }
    }
}

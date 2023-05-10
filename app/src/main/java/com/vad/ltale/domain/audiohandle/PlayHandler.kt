package com.vad.ltale.domain.audiohandle

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R

class PlayHandler(private val player: Player, private val recyclerView: RecyclerView) {

    private lateinit var handler: Handler
    private var oldPosition = -1
    private lateinit var ran: Runnable

    private fun stop(playButton: ShapeableImageView) {
        player.stop()
        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun handle(position: Int, playButton: ShapeableImageView, uri: String, seekBar: SeekBar) {
        handler = Handler(Looper.getMainLooper())

        if (oldPosition != -1 && oldPosition != position) {
            val button = recyclerView.findViewHolderForAdapterPosition(oldPosition)?.itemView?.findViewById<ShapeableImageView>(R.id.playButton)
            val tempSeekBar = recyclerView.findViewHolderForAdapterPosition(oldPosition)?.itemView?.findViewById<SeekBar>(R.id.seekBar)
            tempSeekBar?.progress = 0
            stop(button!!)
            Log.d("##r", "onItemClick: ")
        }
        Log.d("##r", "onItemClick: 2")

        if (player.isPlay) {
            stop(playButton)
            handler.obtainMessage()
        } else {
            player.play(uri)

            seekBar.max = player.mp.duration

            ran = Runnable {
                seekBar.progress = player.mp.currentPosition
                handler.postDelayed(ran, 100)
            }

            handler.post(ran)


            playButton.setImageResource(R.drawable.ic_baseline_pause_24)
        }
        oldPosition = position
    }
}

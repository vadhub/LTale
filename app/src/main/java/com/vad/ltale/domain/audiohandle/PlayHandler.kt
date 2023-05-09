package com.vad.ltale.domain.audiohandle

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R

class PlayHandler(private val player: Player, private val recyclerView: RecyclerView) {

    private var oldPosition = -1

    private fun switch(playButton: ShapeableImageView, uri: String) {
        if (player.isPlay) {
            stop(playButton)
        } else {
            player.play(uri)
            playButton.setImageResource(R.drawable.ic_baseline_pause_24)
        }
    }

    private fun stop(playButton: ShapeableImageView) {
        player.stop()
        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun handle(position: Int, playButton: ShapeableImageView, uri: String) {
        if (oldPosition != -1 && oldPosition != position) {
            val button = recyclerView.findViewHolderForAdapterPosition(oldPosition)?.itemView?.findViewById<ShapeableImageView>(R.id.playButton)
            stop(button!!)
            Log.d("##r", "onItemClick: ")
        }
        Log.d("##r", "onItemClick: 2")
        switch(playButton, uri)
        oldPosition = position
    }
}

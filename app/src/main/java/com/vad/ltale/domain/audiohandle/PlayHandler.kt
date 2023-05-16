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
    private var oldAdapter: AudioAdapter? = null

    fun handle(position: Int, audio: Audio, localUri: String, audioAdapter: AudioAdapter, seekBar: SeekBar) {
        handler = Handler(Looper.getMainLooper())
        if (oldPosition != -1 && oldPosition != position) {
            oldAdapter?.notifyItemChanged(oldPosition, audioTemp)
        }

        if (player.isPlay) {
            player.stop()
            audioAdapter.notifyItemChanged(position, audio)
        } else {
            player.play(localUri)

            ran = Runnable {
                seekBar.progress = ((player.mp.currentPosition*100)/player.mp.duration).toInt()
                handler.postDelayed(ran, 100)
            }

            handler.post(ran)
        }

        player.mp.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_ENDED) {
                    audioAdapter.notifyItemChanged(position, audio.also { it.isPlay = false })
                    seekBar.progress = 0
                }
            }
        })

        oldAdapter = audioAdapter

        oldPosition = position

        audioTemp = audio.also { it.isPlay = false }
    }
}

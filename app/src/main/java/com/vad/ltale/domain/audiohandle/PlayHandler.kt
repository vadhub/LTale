package com.vad.ltale.domain.audiohandle

import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.vad.ltale.data.PlayView
import com.vad.ltale.presentation.adapter.AudioAdapter

class PlayHandler(private val player: Player) {

    private lateinit var handler: Handler
    private lateinit var ran: Runnable
    private var isPlay = false

    private var oldPosition = -1
    private var uriTemp = ""
    private var oldAdapter: AudioAdapter? = null

    fun handle (
        position: Int,
        localUri: String,
        audioAdapter: AudioAdapter,
        seekBar: SeekBar
    ) {

        handler = Handler(Looper.getMainLooper())

        if (oldPosition != -1 && isPlay) {
            if (oldAdapter != audioAdapter) {
                oldAdapter?.notifyItemChanged(oldPosition)
            }
        }

        if (localUri != uriTemp) {
            isPlay = false
            if (oldAdapter == audioAdapter) {
                audioAdapter.notifyItemChanged(oldPosition)
            }
        }

        uriTemp = localUri

        if (isPlay) {
            player.stop()
            isPlay = false
            if (oldPosition == position) {
                audioAdapter.notifyItemChanged(position)
            }
        } else {
            isPlay = true
            player.play(localUri)
            ran = Runnable {
                seekBar.progress = ((player.mp.currentPosition*100)/player.mp.duration).toInt()
                handler.postDelayed(ran, 100)
            }

            handler.post(ran)

            player.mp.addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == androidx.media3.common.Player.STATE_ENDED) {
                        isPlay = false
                        oldAdapter?.notifyItemChanged(oldPosition)
                        seekBar.progress = 0
                    }
                }
            })
        }

        oldAdapter = audioAdapter
        oldPosition = position

    }

    fun handlePlayView (
        playView: PlayView
    ) {

        handler = Handler(Looper.getMainLooper())

        if (oldPosition != -1 && isPlay) {
            oldAdapter?.notifyItemChanged(oldPosition)
        }

        if (playView.audio.uri != uriTemp) {
            isPlay = false
            playView.audioAdapter.notifyItemChanged(oldPosition)
        }

        uriTemp = playView.audio.uri

        if (isPlay) {
            player.stop()
            isPlay = false
            if (oldPosition == playView.position) {
                playView.audioAdapter.notifyItemChanged(playView.position)
            }
        } else {
            isPlay = true
            player.play(playView.audio.uri)
            ran = Runnable {
                playView.seekBar.progress = ((player.mp.currentPosition*100)/player.mp.duration).toInt()
                handler.postDelayed(ran, 100)
            }

            handler.post(ran)

            player.mp.addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == androidx.media3.common.Player.STATE_ENDED) {
                        isPlay = false
                        playView.audioAdapter.notifyItemChanged(oldPosition)
                        playView.seekBar.progress = 0
                    }
                }
            })
        }

        oldPosition = playView.position

    }
}

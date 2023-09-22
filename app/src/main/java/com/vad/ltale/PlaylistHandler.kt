package com.vad.ltale

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vad.ltale.data.TimeFormatter
import com.vad.ltale.presentation.adapter.AudioAdapter

class PlaylistHandler(
    private val player: ExoPlayer,
    private val play: (layoutPositionParent: Int, idAudio: Int, () -> Unit) -> Unit,
) {

    private var playingParentPosition = -1
    private var playingChildPosition = -1
    private var durationBeforeDicrease = ""
    private var handler = Handler(Looper.getMainLooper())

    private var playingChildHolder: AudioAdapter.RecordViewHolder? = null
    private var clickedHolder: AudioAdapter.RecordViewHolder? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    seekBarChanged(playingChildHolder!!.timeTextView, playingChildHolder!!.seekBar, player.duration)
                }
            }
        })
    }

    private fun updateNonPlayingChild(playingHolder: AudioAdapter.RecordViewHolder?) {
        handler.removeCallbacksAndMessages(null)
        playingHolder?.timeTextView?.text = durationBeforeDicrease
        playingHolder?.seekBar?.max = 0
        playingHolder?.playButton?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun updateNonPlayingLastChild() {
        updateNonPlayingChild(playingChildHolder)
    }

    private fun updatePlayingView() {
        if (player.playWhenReady) {
            playingChildHolder?.playButton?.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playingChildHolder?.playButton?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    fun play(
        positionParent: Int,
        positionChild: Int,
        holder: AudioAdapter.RecordViewHolder,
        audioId: Int
    ) {

        if (positionChild == playingChildPosition && positionParent == playingParentPosition) {
            if (player.playWhenReady) {
                player.pause()
            } else {
                player.play()
            }
            updatePlayingView()
        } else {
            player.stop()
            updateNonPlayingChild(playingChildHolder)

            holder.progressBar.visibility = View.VISIBLE
            holder.playButton.visibility = View.INVISIBLE

            clickedHolder?.progressBar?.visibility = View.GONE
            clickedHolder?.playButton?.visibility = View.VISIBLE

            clickedHolder = holder
            val changePlayItem: () -> Unit = {
                playingParentPosition = positionParent
                playingChildPosition = positionChild
                playingChildHolder = holder
                updatePlayingView()
                holder.playButton.visibility = View.VISIBLE
                holder.progressBar.visibility = View.INVISIBLE
            }

            play.invoke(
                positionParent,
                audioId,
                changePlayItem
            )
        }

    }

    private fun seekBarChanged(timeTextView: TextView, seekBar: SeekBar, duration: Long) {

        durationBeforeDicrease = TimeFormatter.format(player.duration)
        timeTextView.text = durationBeforeDicrease

        val runnable = object: Runnable {
            override fun run() {

                val progress = ((player.currentPosition * 100)/duration).toInt()

                timeTextView.text = TimeFormatter.format(player.duration - player.currentPosition)
                seekBar.progress = progress
                handler.postDelayed(this, 1000)
            }
        }

        handler.postDelayed(runnable, 0)
    }

}
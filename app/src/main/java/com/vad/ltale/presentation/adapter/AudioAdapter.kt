package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.Audio
import java.util.concurrent.TimeUnit


class AudioAdapter(private val player: ExoPlayer) : Adapter<AudioAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()

    private var playingPosition = -1

    fun resetPlayingPosition() {
        playingPosition = -1
    }
    private var playingHolder: RecordViewHolder? = null

    private var onClick: ((RecordViewHolder?) -> Unit)? = null

    fun setOnClick(onClick: (RecordViewHolder?) -> Unit) {
        this.onClick = onClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(audios: List<Audio>) {
        this.audio = audios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        if (position == playingPosition) {
            playingHolder = holder
            updatePlayingView()
        } else {
            updateNonPlaying(playingHolder)
        }
        holder.bind(duration = audio.get(position).duration)
    }

    override fun getItemCount(): Int = audio.size

    inner class RecordViewHolder(item: View) : ViewHolder(item), View.OnClickListener {
        val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.playButton)
        val seekBar: SeekBar = item.findViewById(R.id.seekBar)
        val progressBar: ProgressBar = item.findViewById(R.id.loadingProgressBar)

        @SuppressLint("SetTextI18n")
        fun bind(duration: Long) {
            val mTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))
            timeTextView.text = mTime
            playButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("!click", "click")

            onClick?.invoke(this)

            if (layoutPosition == playingPosition) {
                Log.d("!click", "1")
                if (player.playWhenReady) {
                    Log.d("!click", "1 1")
                    player.pause()
                } else {
                    Log.d("!click", "1 2")
                    player.play()
                }
            } else {
                player.stop()
                updateNonPlaying(playingHolder)
                Log.d("!click", "2")
                playingPosition = layoutPosition
                playingHolder = this
                startPlaying(audio[layoutPosition].uri)
            }

            updatePlayingView()
        }
    }

    fun updateNonPlaying(playingHolder: RecordViewHolder?) {
        Log.d("1 item update", "$playingHolder")
        playingHolder?.playButton?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun updateNonPlayingLast() {
        updateNonPlaying(playingHolder)
    }

    fun updatePlayingView() {

        if (player.playWhenReady) {
            playingHolder?.playButton?.setImageResource(R.drawable.ic_baseline_pause_24)
            Log.d("##adapter", "play 1 ")
        } else {
            playingHolder?.playButton?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            Log.d("##adapter", "pause  ")
        }
    }

    private fun startPlaying(uri: String) {
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()

        Log.d("##start", "play ${player.duration} ")
    }

}
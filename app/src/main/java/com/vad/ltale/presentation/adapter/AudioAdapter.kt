package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import java.util.concurrent.TimeUnit


class AudioAdapter(private var clickListener: RecyclerOnClickListener) : Adapter<AudioAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()
    private lateinit var parentRecyclerView: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(audios: List<Audio>) {
        this.audio = audios
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.parentRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(duration = audio.get(position).duration)

        if (!audio.get(0).isPlay) holder.reset()

        holder.also { h ->
            h.playButton.setOnClickListener {
                clickListener.onItemClick(position, audio.get(position), this, h.seekBar, h.progressBar)
                h.playButton.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.playButton)
        val seekBar: SeekBar = item.findViewById(R.id.seekBar)
        val progressBar: ProgressBar = item.findViewById(R.id.loadingProgressBar)

        fun reset() {
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            seekBar.progress = 0
        }

        @SuppressLint("SetTextI18n")
        fun bind(duration: Long) {
            val mTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))
            timeTextView.text = mTime
        }
    }

}
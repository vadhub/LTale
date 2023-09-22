package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.Audio
import com.vad.ltale.model.TimeFormatter
import com.vad.ltale.model.audiohandle.PlaylistHandler

class AudioAdapter(private val parentPosition: Int, private val playlistHandler: PlaylistHandler) :
    Adapter<AudioAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()

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
        holder.bind(duration = audio.get(position).duration)
    }

    override fun getItemCount(): Int = audio.size

    inner class RecordViewHolder(item: View) : ViewHolder(item) {
        val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.playButton)
        val seekBar: SeekBar = item.findViewById(R.id.seekBar)
        val progressBar: ProgressBar = item.findViewById(R.id.loadingProgressBar)

        @SuppressLint("SetTextI18n")
        fun bind(duration: Long) {
            timeTextView.text = TimeFormatter.format(duration)
            playButton.setOnClickListener {
                playlistHandler.play(parentPosition, layoutPosition, this, audio[layoutPosition])
            }
        }
    }


}
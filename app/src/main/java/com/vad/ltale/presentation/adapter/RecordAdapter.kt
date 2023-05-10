package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio


class RecordAdapter(private var clickListener: RecyclerOnClickListener) : Adapter<RecordAdapter.RecordViewHolder>() {

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

        holder.also { h ->
            h.playButton.setOnClickListener {
                clickListener.onItemClick(position, h.playButton, h.seekBar)
            }
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.playButton)
        val seekBar: SeekBar = item.findViewById(R.id.seekBar)

        @SuppressLint("SetTextI18n")
        fun bind(duration: Long) {
            val minutes = duration / 1000 / 60
            val seconds = duration / 1000 % 60
            timeTextView.text = "$minutes:$seconds"
        }
    }

}
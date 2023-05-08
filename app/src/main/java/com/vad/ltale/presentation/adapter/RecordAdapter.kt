package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.AudioRequest
import com.vad.ltale.domain.RecyclerOnClickListener
import java.io.File


class RecordAdapter : Adapter<RecordAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()
    private var clickListener: RecyclerOnClickListener? = null

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
        holder.playButton.setOnClickListener {
            clickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {
        private val titleTextView: TextView = item.findViewById(R.id.audioTitleTextView)
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.handleAudioImageButton)

        @SuppressLint("SetTextI18n")
        fun bind(title: String = "", duration: Long) {
            titleTextView.text = title
            val minutes = duration / 1000 / 60
            val seconds = duration / 1000 % 60
            timeTextView.text = "$minutes:$seconds"
        }
    }

}
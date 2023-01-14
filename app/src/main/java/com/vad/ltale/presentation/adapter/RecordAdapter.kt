package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vad.ltale.R
import com.vad.ltale.data.FileResponse
import com.vad.ltale.domain.PlayAudioHandle


class RecordAdapter : Adapter<RecordAdapter.RecordViewHolder>() {

    private var audio: List<FileResponse> = emptyList()
    private val playAudio = PlayAudioHandle()

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(audios: List<FileResponse>) {
        this.audio = audio
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind("", playAudio.setAudioSource(audio.get(position).uri))
        holder.playButton.setOnClickListener {
            playAudio.playAudio()
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {

        private val titleTextView: TextView = item.findViewById(R.id.audioTitleTextView)
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ImageButton = item.findViewById(R.id.handleAudioImageButton)

        fun bind(title: String, duration: Int) {
            titleTextView.text = title
            timeTextView.text = "$duration"
        }
    }

}
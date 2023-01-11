package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vad.ltale.R
import com.vad.ltale.data.Audio
import com.vad.ltale.data.AudioRequest


class RecordAdapter : Adapter<RecordAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()
    private var mediaPlayer: MediaPlayer? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(audios: List<Audio>) {
        this.audio = audio
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        //holder.bind(records.get(position), 0)
        holder.playButton.setOnClickListener {
            playAudio(audio.get(position).uri)
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {

        private val titleTextView: TextView = item.findViewById(R.id.audioTitleTextView)
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ImageButton = item.findViewById(R.id.handleAudioImageButton)

        fun bind(title: String, duration: Long) {
            titleTextView.text = title
            timeTextView.text = "$duration"
        }
    }

    private fun playAudio(audioSource: String) {

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(audioSource)
            prepare()
            start()
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
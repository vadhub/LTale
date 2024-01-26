package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.timehandle.TimeFormatter
import com.vad.ltale.model.audiohandle.PlaylistHandler

class AudioAdapter(private val parentPosition: Int, private val playlistHandler: PlaylistHandler, private val isRecord: Boolean) :
    Adapter<AudioAdapter.RecordViewHolder>() {

    private var audio: List<Audio> = emptyList()

    var removeListener: (audio: Audio) -> Unit = {}

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
        holder.bind(audio = audio.get(position))
    }

    override fun getItemCount(): Int = audio.size

    inner class RecordViewHolder(item: View) : ViewHolder(item), View.OnClickListener {
        val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.playButton)
        val seekBar: SeekBar = item.findViewById(R.id.seekBar)
        val progressBar: ProgressBar = item.findViewById(R.id.loadingProgressBar)
        val removeButton: ImageButton = item.findViewById(R.id.removeButton)
        private var audio = Audio(-1,"", 0L, "")

        @SuppressLint("SetTextI18n")
        fun bind(audio: Audio) {

            this.audio = audio

            if (isRecord) {
                removeButton.visibility = View.VISIBLE
            }

            removeButton.setOnClickListener(this)
            playButton.setOnClickListener(this)

            timeTextView.text = TimeFormatter.format(audio.duration)
        }

        override fun onClick(v: View?) {
            when (v) {
                removeButton -> {removeListener.invoke(audio)}
                playButton -> {playlistHandler.play(parentPosition, layoutPosition, this, audio)}
            }
        }
    }


}
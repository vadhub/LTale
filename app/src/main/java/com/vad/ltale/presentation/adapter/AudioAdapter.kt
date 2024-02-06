package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
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
import com.vad.ltale.databinding.ItemAudioBinding
import com.vad.ltale.model.audiohandle.PlaylistHandler
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.timehandle.TimeFormatter

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
            ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(audio = audio.get(position))
    }

    override fun getItemCount(): Int = audio.size

    inner class RecordViewHolder(item: ItemAudioBinding) : ViewHolder(item.root), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        val timeTextView: TextView = item.audioTimeTextView
        val playButton: ShapeableImageView = item.playButton
        val seekBar: SeekBar = item.seekBar
        val progressBar: ProgressBar = item.loadingProgressBar
        val removeButton: ImageButton = item.removeButton
        private var audio = Audio(-1,"", 0L, "")

        @SuppressLint("SetTextI18n")
        fun bind(audio: Audio) {

            this.audio = audio

            if (isRecord) {
                removeButton.visibility = View.VISIBLE
            }

            removeButton.setOnClickListener(this)
            playButton.setOnClickListener(this)
            seekBar.setOnSeekBarChangeListener(this)

            timeTextView.text = TimeFormatter.format(audio.duration)
        }

        override fun onClick(v: View?) {
            when (v) {
                removeButton -> {removeListener.invoke(audio)}
                playButton -> {playlistHandler.play(parentPosition, layoutPosition, this, audio)}
            }
        }

        override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
            playlistHandler.seekTo(progress, fromUser)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {}

        override fun onStopTrackingTouch(p0: SeekBar?) {}
    }


}
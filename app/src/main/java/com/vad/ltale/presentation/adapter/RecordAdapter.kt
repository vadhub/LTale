package com.vad.ltale.presentation.adapter

import android.annotation.SuppressLint
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.R
import com.vad.ltale.data.FileResponse
import com.vad.ltale.domain.PlayAudioHandle
import com.vad.ltale.domain.RecyclerOnClickListener
import java.io.File


class RecordAdapter : Adapter<RecordAdapter.RecordViewHolder>() {

    private var audio: List<FileResponse> = emptyList()
    private var clickListener: RecyclerOnClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(audios: List<FileResponse>) {
        this.audio = audios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.playButton.setOnClickListener {
            clickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = audio.size

    class RecordViewHolder(item: View) : ViewHolder(item) {
        private val titleTextView: TextView = item.findViewById(R.id.audioTitleTextView)
        private val timeTextView: TextView = item.findViewById(R.id.audioTimeTextView)
        val playButton: ShapeableImageView = item.findViewById(R.id.handleAudioImageButton)

        fun bind(title: String, duration: Int) {
            titleTextView.text = title
            timeTextView.text = "$duration"
        }
    }

}
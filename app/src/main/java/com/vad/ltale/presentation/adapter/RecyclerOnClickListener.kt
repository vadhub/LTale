package com.vad.ltale.presentation.adapter

import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.vad.ltale.data.Audio

interface RecyclerOnClickListener {
    fun onItemClick(position: Int, audio: Audio, audioAdapter: AudioAdapter, seekBar: SeekBar, progressBar: ProgressBar)
}
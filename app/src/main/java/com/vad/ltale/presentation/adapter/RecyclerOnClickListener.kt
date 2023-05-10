package com.vad.ltale.presentation.adapter

import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

interface RecyclerOnClickListener {
    fun onItemClick(position: Int, uri: String, playButton: ShapeableImageView, seekBar: SeekBar, parentRecyclerView: RecyclerView)
}
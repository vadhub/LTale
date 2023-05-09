package com.vad.ltale.presentation.adapter

import com.google.android.material.imageview.ShapeableImageView

interface RecyclerOnClickListener {
    fun onItemClick(position: Int, playButton: ShapeableImageView)
}
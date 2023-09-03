package com.vad.ltale.presentation.adapter

import com.vad.ltale.model.PostResponse

interface LikeOnClickListener {
    fun onLike(post: PostResponse, position: Int)
}
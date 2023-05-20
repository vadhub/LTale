package com.vad.ltale.presentation.adapter

import com.vad.ltale.data.PostResponse

interface LikeOnClickListener {
    fun onLike(post: PostResponse, position: Int)
}
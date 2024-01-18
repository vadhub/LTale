package com.vad.ltale.presentation.adapter

import com.vad.ltale.model.pojo.PostResponse

interface LikeOnClickListener {
    fun onLike(post: PostResponse, position: Int)
}
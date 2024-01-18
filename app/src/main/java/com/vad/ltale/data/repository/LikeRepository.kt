package com.vad.ltale.data.repository

import com.vad.ltale.model.pojo.Like
import com.vad.ltale.data.remote.RemoteInstance

class LikeRepository(private val remoteInstance: RemoteInstance) {

    suspend fun addLike(like: Like) = remoteInstance.apiLike().likePost(like)

    suspend fun deleteLike(like: Like) = remoteInstance.apiLike().deleteLike(like)
}
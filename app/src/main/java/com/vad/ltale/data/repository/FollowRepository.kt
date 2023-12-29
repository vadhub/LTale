package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.model.Follow

class FollowRepository(private val remoteInstance: RemoteInstance) {
    suspend fun subscribe(follow: Follow) = remoteInstance.apiFollow().subscribe(follow)
    suspend fun unsubscribe(follow: Follow) = remoteInstance.apiFollow().unsubscribe(follow)
}
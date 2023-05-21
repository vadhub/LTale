package com.vad.ltale.data.repository

import com.vad.ltale.data.Limit
import com.vad.ltale.data.remote.RemoteInstance
import java.sql.Date

class LimitRepository(private val remoteInstance: RemoteInstance) {

    suspend fun update(id: Long, limit: Limit) =
        remoteInstance.apiLimit().update(id, limit).body() ?: "" //todo create handle error

    suspend fun getByUserId(userId: Long): Limit =
        remoteInstance.apiLimit().getLimit(userId).body() ?: Limit(-1,-1,-1, Date(0))  //todo create handle error

}
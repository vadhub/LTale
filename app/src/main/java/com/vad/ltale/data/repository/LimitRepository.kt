package com.vad.ltale.data.repository

import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.Resource
import com.vad.ltale.data.remote.exception.GetTimeException
import com.vad.ltale.data.remote.exception.UpdateException
import com.vad.ltale.model.pojo.Limit

class LimitRepository(private val remoteInstance: RemoteInstance) {

    suspend fun update(id: Long, limit: Limit): Resource<Boolean> {
        val response = remoteInstance.apiLimit().update(id, limit)

        if (response.isSuccessful) {
            return Resource.Success(response.isSuccessful)
        }

        return Resource.Failure(UpdateException())
    }

    suspend fun getByUserId(userId: Long): Resource<Limit> {
        val response = remoteInstance.apiLimit().getLimit(userId)

        if (response.isSuccessful) {
            val body = response.body()

            return if (body != null) {
                Resource.Success(body)
            } else {
                Resource.Failure(GetTimeException())
            }

        }

        return Resource.Failure(GetTimeException())
    }
}
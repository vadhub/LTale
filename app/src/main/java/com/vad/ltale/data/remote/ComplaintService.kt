package com.vad.ltale.data.remote

import com.vad.ltale.model.pojo.ComplaintReport
import retrofit2.http.Body
import retrofit2.http.POST

interface ComplaintService {

    @POST("/api-v1/complaint")
    suspend fun sendReport(@Body report: ComplaintReport)

}
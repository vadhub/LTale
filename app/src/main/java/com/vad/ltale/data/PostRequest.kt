package com.vad.ltale.data

import okhttp3.MultipartBody
import java.sql.Date

data class PostRequest(val audio: MultipartBody, val image: MultipartBody?, val userId: Int, val dateCreated: Date, val dateChanged: Date)
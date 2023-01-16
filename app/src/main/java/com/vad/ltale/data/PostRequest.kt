package com.vad.ltale.data

import okhttp3.MultipartBody
import java.sql.Date

data class PostRequest(
    val audio: MultipartBody.Part,
    val image: MultipartBody.Part?,
    val userId: Int,
    val dateCreated: Long,
    val dateChanged: Long
    )
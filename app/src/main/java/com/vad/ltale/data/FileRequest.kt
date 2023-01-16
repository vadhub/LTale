package com.vad.ltale.data

import okhttp3.MultipartBody
import retrofit2.http.Part
import java.sql.Date

class FileRequest(val file: MultipartBody.Part, val dateCreated: Long, val dateChanged: Long)

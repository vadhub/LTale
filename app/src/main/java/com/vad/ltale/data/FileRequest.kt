package com.vad.ltale.data

import okhttp3.MultipartBody
import retrofit2.http.Part
import java.sql.Date
import kotlin.time.Duration

class FileRequest(val file: MultipartBody.Part, val duration: Duration)

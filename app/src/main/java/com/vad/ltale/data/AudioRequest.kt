package com.vad.ltale.data

import okhttp3.MultipartBody
import java.sql.Date

data class AudioRequest(val file: MultipartBody, val dateCreated: Date, val dateChanged: Date)

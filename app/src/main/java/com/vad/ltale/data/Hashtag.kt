package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hashtag(
    @SerializedName("hashtagName")
    @Expose
    val hashtagName: String
    )

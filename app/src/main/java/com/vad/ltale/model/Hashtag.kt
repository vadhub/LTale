package com.vad.ltale.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hashtag(
    @SerializedName("hashtagName")
    @Expose
    val hashtagName: String
    )

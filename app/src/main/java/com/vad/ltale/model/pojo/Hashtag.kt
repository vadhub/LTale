package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hashtag(
    @SerializedName("hashtagName")
    @Expose
    val hashtagName: String
    )

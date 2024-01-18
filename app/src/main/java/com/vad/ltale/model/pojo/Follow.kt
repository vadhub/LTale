package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Follow(
    @SerializedName("followerId")
    @Expose
    val followerId: Long,

    @SerializedName("followedId")
    @Expose
    val followedId: Long
)

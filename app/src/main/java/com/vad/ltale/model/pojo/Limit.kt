package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Limit(

    @SerializedName("id")
    @Expose
    val id: Long,

    @SerializedName("userId")
    @Expose
    val userId: Long,

    @SerializedName("time")
    @Expose
    val time: Long,

    @SerializedName("dateUpdate")
    val dateUpdate: String
)

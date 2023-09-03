package com.vad.ltale.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Date

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

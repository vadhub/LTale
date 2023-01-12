package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Date

data class PostResponse(

    @SerializedName("audioId")
    @Expose
    val audioId: Long,

    @SerializedName("imageId")
    @Expose
    val imageId: Long,

    @SerializedName("userId")
    @Expose
    val userId: Long,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: Date,

    @SerializedName("dateChanged")
    @Expose
    val dateChanged: Date
)

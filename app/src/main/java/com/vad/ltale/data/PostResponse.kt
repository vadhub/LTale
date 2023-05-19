package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Date

data class PostResponse(

    @SerializedName("id")
    @Expose
    val audioId: Int,

    @SerializedName("image")
    @Expose
    val image: Image?,

    @SerializedName("userId")
    @Expose
    val userId: Int,

    @SerializedName("audioList")
    @Expose
    val listAudio: List<Audio>,

    @SerializedName("liked")
    @Expose
    val isLiked: Boolean,

    @SerializedName("countLike")
    @Expose
    val countLike: Int,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

    @SerializedName("dateChanged")
    @Expose
    val dateChanged: String,
)

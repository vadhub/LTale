package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostResponse(

    @SerializedName("id")
    @Expose
    val postId: Int,

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
    var isLiked: Boolean,

    @SerializedName("countLike")
    @Expose
    var countLike: Int,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

    @SerializedName("dateChanged")
    @Expose
    val dateChanged: String,
)

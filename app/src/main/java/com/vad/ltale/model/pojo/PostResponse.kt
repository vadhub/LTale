package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vad.ltale.model.pojo.Audio
import com.vad.ltale.model.pojo.Hashtag
import com.vad.ltale.model.pojo.Image

data class PostResponse(

    @SerializedName("id")
    @Expose
    val postId: Long,

    @SerializedName("image")
    @Expose
    val image: Image?,

    @SerializedName("user")
    @Expose
    val userId: Long,

    @SerializedName("nikName")
    @Expose
    val nikName: String,

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

    @SerializedName("hashtags")
    @Expose
    val hashtags: List<Hashtag>
)

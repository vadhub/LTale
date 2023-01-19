package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Date

data class PostResponse(

    @SerializedName("userId")
    @Expose
    val userId: Int,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

    @SerializedName("dateChanged")
    @Expose
    val dateChanged: String,

    @SerializedName("_links")
    @Expose
    val links: Links
)

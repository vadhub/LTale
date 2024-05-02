package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ComplaintReport(
    @SerializedName("post")
    @Expose
    val post: String,

    @SerializedName("idComplaint")
    @Expose
    val idComplaint: Int,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

)

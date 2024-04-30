package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ComplaintReport(
    @SerializedName("id")
    @Expose
    val postId: Long,

    @SerializedName("id_complaint")
    @Expose
    val idComplaint: Int,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

)

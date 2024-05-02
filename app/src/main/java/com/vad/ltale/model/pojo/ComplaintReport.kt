package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ComplaintReport(
    @SerializedName("post")
    @Expose
    val post: String,

    @SerializedName("id_complaint")
    @Expose
    val idComplaint: Int,

    @SerializedName("date_created")
    @Expose
    val dateCreated: String,

)

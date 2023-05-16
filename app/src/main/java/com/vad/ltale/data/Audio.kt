package com.vad.ltale.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Date
import java.sql.Timestamp

@Entity(tableName = "audio_table")
data class Audio(
    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,

    @SerializedName("uri")
    @Expose
    @ColumnInfo(name = "uri")
    var uri: String = "",

    @SerializedName("duration")
    @Expose
    @ColumnInfo(name = "duration")
    val duration: Long,

    @SerializedName("dateCreate")
    @Expose
    @ColumnInfo(name = "date")
    val date: String,

    @Ignore
    var isPlay: Boolean = false
) {
    constructor(id: Long, uri: String, duration: Long, date: String) : this(id, uri, duration, date, false)
}

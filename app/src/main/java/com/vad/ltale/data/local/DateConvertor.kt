package com.vad.ltale.data.local

import androidx.room.TypeConverter
import java.sql.Date
import java.sql.Timestamp

class DateConvertor {

    @TypeConverter
    fun toDate(date: Long?): Date? {
        return date?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
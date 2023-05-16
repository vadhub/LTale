package com.vad.ltale.data.local

import androidx.room.TypeConverter
import java.sql.Date

class DateConvertor {

    @TypeConverter
    fun toDate(date: Long?): Date? {
        return if (date == null) {
            null
        } else {
            Date(date)
        }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
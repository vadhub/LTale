package com.vad.ltale.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vad.ltale.data.Audio

@Database(entities = [Audio::class], version = 1, exportSchema = false)
@TypeConverters(DateConvertor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audioDao(): AudioDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}
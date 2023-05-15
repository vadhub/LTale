package com.vad.ltale.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vad.ltale.data.Audio
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    @Query("SELECT * FROM audio_table")
    fun getAllAudio(): Flow<List<Audio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audio: Audio)

    @Query("DELETE FROM audio_table")
    suspend fun deleteAll()
}
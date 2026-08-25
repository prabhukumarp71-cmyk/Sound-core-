package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY name ASC")
    fun getAllPresets(): Flow<List<Preset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: Preset)
    
    @Update
    suspend fun updatePreset(preset: Preset)

    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deletePresetById(id: Int)
}

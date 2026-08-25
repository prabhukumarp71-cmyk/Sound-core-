package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isCustom: Boolean = true,
    val band31: Float = 0f,
    val band62: Float = 0f,
    val band125: Float = 0f,
    val band250: Float = 0f,
    val band500: Float = 0f,
    val band1k: Float = 0f,
    val band2k: Float = 0f,
    val band4k: Float = 0f,
    val band8k: Float = 0f,
    val band16k: Float = 0f,
    val bassAmount: Float = 0f,
    val midsAmount: Float = 0f,
    val trebleAmount: Float = 0f,
    val preamp: Float = 0f
)

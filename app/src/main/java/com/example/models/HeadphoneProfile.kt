package com.example.models

data class HeadphoneProfile(
    val id: String,
    val name: String,
    val imageResId: Int? = null,
    val isCustom: Boolean = false
)

val defaultHeadphoneProfiles = listOf(
    HeadphoneProfile("q_series", "Soundcore Q Series"),
    HeadphoneProfile("space_series", "Soundcore Space Series"),
    HeadphoneProfile("liberty_series", "Soundcore Liberty Series"),
    HeadphoneProfile("life_series", "Soundcore Life Series"),
    HeadphoneProfile("custom", "Custom Profile", isCustom = true)
)

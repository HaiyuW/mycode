package com.example.cse438.cse438_assignment2.data

data class Radio(
    val id: Int,
    val title: String,
    val picture: String,
    val picture_small: String,
    val picture_medium: String,
    val picture_big: String,
    val picture_xl: String,
    val tracklist: String,
    val type: String
)

// handle the radios
data class RadioData( val data: List<Radio> )

// handle the tracks searched by radios
data class RadioTrack (val data: List<Track>)
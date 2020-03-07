package com.example.cse438.cse438_assignment2.data

data class Song (
    val id: Int,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val title_version: String,
    val isrc: String,
    val link: String,
    val share: String,
    val duration: String,
    val track_position: Int,
    val disk_number: Int,
    val rank: String,
    val release_date: String,
    val explicit_lyrics: Boolean,
    val explicit_content_lyrics: Int,
    val explicit_content_cover: Int,
    val preview: String,
    val bpm: Float,
    val gain: Float,
    val available_countries: List<String>,
    val artist: Artist,
    val album: Album
)
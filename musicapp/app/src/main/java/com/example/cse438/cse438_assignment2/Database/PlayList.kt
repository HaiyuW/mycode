package com.example.cse438.cse438_assignment2.Database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_table")
data class PlayList(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlistId") @NonNull
    val playlistId: Int,
    val playlistName: String,
    val description: String,
    val genre: String,
    val rating: Int
)

@Entity(tableName = "tracklist_table")
data class TrackList(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "tracklistId") @NonNull
    val tracklistId: Int,
    val trackTitle: String,
    var playlistId: Int,
    val artistName: String,
    val time: Int,
    val trackId: Int,
    val trackPreview: String
)

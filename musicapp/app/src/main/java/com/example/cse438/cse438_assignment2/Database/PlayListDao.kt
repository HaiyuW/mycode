package com.example.cse438.cse438_assignment2.Database

import androidx.lifecycle.LiveData
import androidx.room.*

data class ResultList(
    val tracklistId: Int,
    val playlistId: Int,
    val trackTitle: String,
    val artistName: String,
    val time: Int,
    val genre: String,
    val rating: Int,
    val trackId: Int,
    val trackPreview: String
)

@Dao
interface PlayListDao {
    // get playlists
    @Query("SELECT * from playlist_table ORDER BY playlistName ASC")
    fun getAlphabetizedPlayList(): LiveData<List<PlayList>>

    // get tracks
    @Query("SELECT tracklistId, tracklist_table.playlistId, trackTitle, artistName, time, playlist_table.genre, playlist_table.rating, trackId, trackPreview from tracklist_table JOIN playlist_table ON playlist_table.playlistId = tracklist_table.playlistId ORDER BY trackTitle ASC")
    fun getAlphabetizedTrackList(): LiveData<List<ResultList>>

    @Query("SELECT tracklistId, tracklist_table.playlistId, trackTitle, artistName, time, playlist_table.genre, playlist_table.rating, trackId, trackPreview from tracklist_table JOIN playlist_table ON playlist_table.playlistId = tracklist_table.playlistId WHERE tracklist_table.playlistId= :id ORDER BY trackTitle ASC")
    fun getTrackList(id: Int): LiveData<List<ResultList>>

    @Insert(entity = PlayList::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylist(playList: PlayList)

    @Query("DELETE FROM tracklist_table WHERE playlistId= :id")
    fun deleteTrack(id: Int)

    @Query("DELETE FROM playlist_table WHERE playlistId= :id")
    fun deletePlaylist(id: Int)

    @Insert(entity = TrackList::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrackList(trackList: TrackList)

    @Query("DELETE FROM tracklist_table WHERE tracklistId= :id")
    fun deleteTrackList(id:Int)

    @Query("DELETE FROM playlist_table")
    fun deleteAll()
}
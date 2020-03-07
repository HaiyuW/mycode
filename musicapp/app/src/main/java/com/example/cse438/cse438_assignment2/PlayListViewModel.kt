package com.example.cse438.cse438_assignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cse438.cse438_assignment2.Database.*
import kotlinx.coroutines.launch

class PlayListViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: PlayListRepository
    val allPlayLists: LiveData<List<PlayList>>
    val allResultLists: LiveData<List<ResultList>>

    init {
        val playlistDao = PlayListRoomDatabase.getDatabase(application).playListDao()
        repository = PlayListRepository(playlistDao)
        allPlayLists = repository.allPlayLists
        allResultLists = repository.allResultLists!!
    }

    fun insertPlaylist(playList: PlayList) = viewModelScope.launch {
        repository.insertPlaylist(playList)
    }

    fun insertTracklist(trackList: TrackList) = viewModelScope.launch {
        repository.insertTracklist(trackList)
    }

    fun deleteTrack(id: Int) = viewModelScope.launch {
        repository.deleteTrack(id)
    }

    fun deletePlaylist(id: Int) = viewModelScope.launch {
        repository.deletePlaylist(id)
    }

    fun getTrackList(id: Int) = viewModelScope.launch {
        repository.getTrackList(id)
    }

    fun deleteTrackList(id: Int) = viewModelScope.launch {
        repository.deleteTracklist(id)
    }

    fun clear() =viewModelScope.launch {
        repository.clear()
    }

}
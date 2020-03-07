package com.example.cse438.cse438_assignment2.Database

import android.app.Application
import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

class PlayListRepository (private val playListDao: PlayListDao) {

    val allPlayLists: LiveData<List<PlayList>> = playListDao.getAlphabetizedPlayList()

    public var allResultLists: LiveData<List<ResultList>> = playListDao.getAlphabetizedTrackList()


    fun getTrackList(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
           allResultLists = playListDao.getTrackList(id)
        }
    }

    fun insertPlaylist(playList: PlayList) {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.insertPlaylist(playList)
        }
    }

    fun deleteTrack(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.deleteTrack(id)
        }
    }

    fun deletePlaylist(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.deletePlaylist(id)
        }
    }


    fun insertTracklist(trackList: TrackList) {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.insertTrackList(trackList)
        }
    }

    fun deleteTracklist(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.deleteTrackList(id)
        }
    }

    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            playListDao!!.deleteAll()
        }
    }
}
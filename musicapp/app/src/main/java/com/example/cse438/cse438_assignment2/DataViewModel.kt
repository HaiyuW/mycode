package com.example.cse438.cse438_assignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cse438.cse438_assignment2.data.Data
import com.example.cse438.cse438_assignment2.Network.DataRepository
import com.example.cse438.cse438_assignment2.data.RadioData
import com.example.cse438.cse438_assignment2.data.RadioTrack
import com.example.cse438.cse438_assignment2.data.Song

class DataViewModel (application: Application): AndroidViewModel(application) {
    public var dataList: MutableLiveData<Data> = MutableLiveData()
    public var songList: MutableLiveData<Song> = MutableLiveData()
    public var radioList: MutableLiveData<RadioData> = MutableLiveData()
    public var radioTrack: MutableLiveData<RadioTrack> = MutableLiveData()
    public var dataRepository: DataRepository = DataRepository()

    fun getDataByChart() {
        dataRepository.getDataByChart(dataList)
    }

    fun getDataBySearch(data: String) {
        dataRepository.getDataBySearch(dataList,data)
    }

    fun getTrackById(id: Int) {
        dataRepository.getTrackById(songList, id)
    }

    fun getRadio() {
        dataRepository.getRadio(radioList)
    }

    fun getTrackByRadio(radioId: Int) {
        dataRepository.getTrackByRadio(radioTrack, radioId)
    }

}
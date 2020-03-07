package com.example.cse438.cse438_assignment2.Network

import com.example.cse438.cse438_assignment2.data.Data
import com.example.cse438.cse438_assignment2.data.RadioData
import com.example.cse438.cse438_assignment2.data.RadioTrack
import com.example.cse438.cse438_assignment2.data.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

interface DataInterface {
    @GET("chart/0/tracks")
    suspend fun getDateByChart(): Response<Data>


    @GET("search")
    suspend fun getDataBySearch(@Query("q") data: String) : Response<Data>

    @GET("track/{id}")
    suspend fun getTrackById(@Path("id") id: Int): Response<Song>

    @GET("radio")
    suspend fun getRadio(): Response<RadioData>

    @GET("radio/{radioId}/tracks")
    suspend fun getTrackByRadio(@Path("radioId") radioId: Int): Response<RadioTrack>
}
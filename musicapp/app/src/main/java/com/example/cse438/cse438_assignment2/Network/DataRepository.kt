package com.example.cse438.cse438_assignment2.Network

import androidx.lifecycle.MutableLiveData
import com.example.cse438.cse438_assignment2.data.Data
import com.example.cse438.cse438_assignment2.data.RadioData
import com.example.cse438.cse438_assignment2.data.RadioTrack
import com.example.cse438.cse438_assignment2.data.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class DataRepository {
    val service = ApiClient.makeRetrofitService()

    fun getDataByChart(resBody: MutableLiveData<Data>) {
        CoroutineScope(Dispatchers.IO).launch {

            val response = service.getDateByChart()

            //lateinit var response: Response<Data>
            //response = service.getDateByChart()

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        resBody.value = response.body()
                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }

    fun getDataBySearch(resBody: MutableLiveData<Data>, dataName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lateinit var response: Response<Data>

            response = service.getDataBySearch(dataName)

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        resBody.value = response.body()
                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }

    fun getTrackById(resBody: MutableLiveData<Song>, id : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            lateinit var response: Response<Song>

            response = service.getTrackById(id)

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        resBody.value = response.body()
                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }

    fun getRadio(resBody: MutableLiveData<RadioData>) {
        CoroutineScope(Dispatchers.IO).launch {
            lateinit var response: Response<RadioData>

            response = service.getRadio()

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        resBody.value = response.body()
                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }

    fun getTrackByRadio(resBody: MutableLiveData<RadioTrack>, radioId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            lateinit var response: Response<RadioTrack>

            response = service.getTrackByRadio(radioId)

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        resBody.value = response.body()
                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }


}
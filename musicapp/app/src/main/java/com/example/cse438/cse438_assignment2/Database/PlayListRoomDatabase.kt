package com.example.cse438.cse438_assignment2.Database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = arrayOf(PlayList::class, TrackList::class), version = 2, exportSchema = false)
public abstract class PlayListRoomDatabase : RoomDatabase() {
    abstract fun playListDao() : PlayListDao

    companion object {

        @Volatile
        private var INSTANCE: PlayListRoomDatabase? = null

        fun getDatabase(context: Context): PlayListRoomDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayListRoomDatabase::class.java,
                    "playlist_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}
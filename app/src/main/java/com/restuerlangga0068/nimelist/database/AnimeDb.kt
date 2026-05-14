package com.restuerlangga0068.nimelist.database

import com.restuerlangga0068.nimelist.data.local.AnimeDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnimeEntity::class], version = 1, exportSchema = false)
abstract class AnimeDb : RoomDatabase() {

    abstract fun dao(): AnimeDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDb? = null

        fun getInstance(context: Context): AnimeDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AnimeDb::class.java,
                        "anime.db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

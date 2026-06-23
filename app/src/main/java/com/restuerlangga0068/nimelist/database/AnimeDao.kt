package com.restuerlangga0068.nimelist.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime")
    fun getAllAnime(): Flow<List<AnimeEntity>>




    @Query("SELECT * FROM anime WHERE id = :id")
    suspend fun getAnimeById(id: String): AnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeList(anime: List<AnimeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)


    @Query("DELETE FROM anime WHERE id = :id")
    suspend fun deleteAnimeById(id: String)
}

package com.restuerlangga0068.nimelist.data.api

import com.restuerlangga0068.nimelist.database.AnimeEntity
import retrofit2.Response
import retrofit2.http.*

interface AnimeApiService {


    @GET("rest/v1/anime?select=*")
    suspend fun getAllAnime(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String
    ): List<AnimeEntity>


    @POST("rest/v1/anime")
    suspend fun insertAnime(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body anime: AnimeEntity
    ): Response<Unit>


    @DELETE("rest/v1/anime")
    suspend fun deleteAnime(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("id") eqId: String
    ): Response<Unit>
}
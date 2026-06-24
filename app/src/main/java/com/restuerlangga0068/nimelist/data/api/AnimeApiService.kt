    package com.restuerlangga0068.nimelist.data.api

    import com.restuerlangga0068.nimelist.database.AnimeEntity
    import retrofit2.Response
    import retrofit2.http.*

    interface AnimeApiService {

        @GET("rest/v1/anime")
        suspend fun getAllAnime(
            @Header("apikey") apiKey: String,
            @Header("Authorization") bearerToken: String,
            @Query("email") emailFilter: String
        ): List<AnimeEntity>

        @Headers(
            "Content-Type: application/json",
            "Prefer: return=minimal"
        )
        @PATCH("rest/v1/anime")
        suspend fun updateAnime(
            @Header("apikey") apiKey: String,
            @Header("Authorization") token: String,
            @Query("id") idFilter: String,
            @Body anime: AnimeEntity
        ): Response<Unit>

        @Headers(
            "Content-Type: application/json",
            "Prefer: return=minimal"
        )
        @POST("rest/v1/anime")
        suspend fun insertAnime(
            @Header("apikey") apiKey: String,
            @Header("Authorization") bearerToken: String,
            @Body anime: List<AnimeEntity>
        ): Response<Unit>

        @Headers("Prefer: return=minimal")
        @DELETE("rest/v1/anime")
        suspend fun deleteAnime(
            @Header("apikey") apiKey: String,
            @Header("Authorization") bearerToken: String,
            @Query("id") eqId: String
        ): Response<Unit>
    }
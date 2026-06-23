package com.restuerlangga0068.nimelist.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://gafftpsctozpxllofjrs.supabase.co"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Ini yang harus ada agar bisa diakses di MainViewModel
    val animeApiService: AnimeApiService = retrofit.create(AnimeApiService::class.java)
}

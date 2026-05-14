package com.restuerlangga0068.nimelist.data

import com.restuerlangga0068.nimelist.R


data class Anime(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val trailerUrl: String,
    val rating: Int = 0,
    val review: String = "",
    val isCompleted: Boolean = false
)

val initialAnimeData = listOf(
    Anime(1, "Bleach", "Kisah Shinigami pembasmi Hollow.", R.drawable.poster3, "https://youtu.be/W99Ef2LkyOg"),
    Anime(2, "Naruto", "Perjalanan Uzumaki Naruto menjadi Hokage.", R.drawable.poster2, "https://youtu.be/QczGoCmX-pI"),
    Anime(3, "One Piece", "Petualangan Luffy mencari harta karun.", R.drawable.poster1, "https://youtu.be/lgAwlnGLTUg"),
    Anime(4, "Bleach 2", "Data Dummy untuk tes scroll.", R.drawable.poster3, "https://youtu.be/W99Ef2LkyOg"),
    Anime(5, "Naruto 2", "Data Dummy untuk tes scroll.", R.drawable.poster2, "https://youtu.be/QczGoCmX-pI"),
    Anime(6, "One Piece 2", "Data Dummy untuk tes scroll.", R.drawable.poster1, "https://youtu.be/lgAwlnGLTUg"),
    Anime(7, "Bleach 3", "Data Dummy untuk tes scroll.", R.drawable.poster3, "https://youtu.be/W99Ef2LkyOg")
)
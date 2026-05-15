package com.restuerlangga0068.nimelist.data


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

